package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.local.CourseDao
import com.example.learningdashboard.data.local.CourseEntity
import com.example.learningdashboard.data.local.CourseWithLessons
import com.example.learningdashboard.data.local.LessonEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.remote.CourseDto
import com.example.learningdashboard.data.remote.LessonDto
import com.example.learningdashboard.domain.repository.RefreshOutcome
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CourseRepositoryImplTest {

    private val pythonCourse = CourseDto(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        progress = 65,
        lessons = (1..20).map { LessonDto(id = 100 + it, title = "Lesson $it") },
    )

    @Test
    fun `successful refresh stores courses and seeds completion from server progress`() = runTest {
        val api = FakeCourseApi(response = { listOf(pythonCourse) })
        val repository = CourseRepositoryImpl(api, FakeCourseDao(), StandardTestDispatcher(testScheduler))

        val outcome = repository.refreshCourses()
        val courses = repository.observeCourses().first()

        assertEquals(RefreshOutcome.Success, outcome)
        assertEquals(1, courses.size)
        assertEquals(20, courses[0].lessonCount)
        assertEquals(13, courses[0].completedCount)
        assertEquals(65, courses[0].progressPercent)
    }

    @Test
    fun `when offline the refresh reports Offline and previously cached courses are still served`() = runTest {
        val dao = FakeCourseDao()
        val api = FakeCourseApi(response = { listOf(pythonCourse) })
        val repository = CourseRepositoryImpl(api, dao, StandardTestDispatcher(testScheduler))
        repository.refreshCourses() // first load succeeds and populates the cache

        api.response = { throw IOException("Unable to resolve host") }
        val outcome = repository.refreshCourses()
        val courses = repository.observeCourses().first()

        assertEquals(RefreshOutcome.Offline, outcome)
        assertEquals("Python Programming", courses.single().title)
        assertEquals(65, courses.single().progressPercent)
    }

    @Test
    fun `a lesson completed locally stays completed after the next refresh`() = runTest {
        val api = FakeCourseApi(response = { listOf(pythonCourse) })
        val repository = CourseRepositoryImpl(api, FakeCourseDao(), StandardTestDispatcher(testScheduler))
        repository.refreshCourses()

        val pendingLesson = repository.observeCourse(1).first()!!.lessons.first { !it.isCompleted }
        repository.markLessonCompleted(pendingLesson.id)
        repository.refreshCourses() // server still says 65%, device knows better

        val course = repository.observeCourse(1).first()!!
        assertTrue(course.lessons.first { it.id == pendingLesson.id }.isCompleted)
        assertEquals(14, course.completedCount)
        assertEquals(70, course.progressPercent)
    }

    @Test
    fun `a server error is reported as Failed, not Offline`() = runTest {
        val api = FakeCourseApi(response = {
            throw HttpException(Response.error<Any>(500, "boom".toResponseBody()))
        })
        val repository = CourseRepositoryImpl(api, FakeCourseDao(), StandardTestDispatcher(testScheduler))

        val outcome = repository.refreshCourses()

        assertTrue(outcome is RefreshOutcome.Failed)
    }
}

private class FakeCourseApi(var response: suspend () -> List<CourseDto>) : CourseApi {
    override suspend fun getCourses(): List<CourseDto> = response()
}

/** In-memory stand-in for the Room DAO. Keeps the same transactional semantics the tests care about. */
private class FakeCourseDao : CourseDao {
    private val courses = MutableStateFlow<Map<Int, CourseEntity>>(emptyMap())
    private val lessons = MutableStateFlow<Map<Int, LessonEntity>>(emptyMap())

    private fun join(course: CourseEntity) =
        CourseWithLessons(course, lessons.value.values.filter { it.courseId == course.id })

    override fun observeCoursesWithLessons(): Flow<List<CourseWithLessons>> =
        courses.map { map -> map.values.sortedBy { it.id }.map(::join) }

    override fun observeCourseWithLessons(courseId: Int): Flow<CourseWithLessons?> =
        courses.map { map -> map[courseId]?.let(::join) }

    override suspend fun getCompletedLessonIds(): List<Int> =
        lessons.value.values.filter { it.isCompleted }.map { it.id }

    override suspend fun upsertCourses(courses: List<CourseEntity>) =
        this.courses.update { it + courses.associateBy { c -> c.id } }

    override suspend fun upsertLessons(lessons: List<LessonEntity>) =
        this.lessons.update { it + lessons.associateBy { l -> l.id } }

    override suspend fun deleteCoursesNotIn(keepIds: List<Int>) {
        courses.update { it.filterKeys { id -> id in keepIds } }
        lessons.update { it.filterValues { l -> l.courseId in keepIds } }
    }

    override suspend fun markLessonCompleted(lessonId: Int) =
        lessons.update { map -> map[lessonId]?.let { map + (lessonId to it.copy(isCompleted = true)) } ?: map }
}
