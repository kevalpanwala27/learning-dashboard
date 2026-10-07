package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.local.CourseDao
import com.example.learningdashboard.data.local.CourseEntity
import com.example.learningdashboard.data.local.CourseWithLessons
import com.example.learningdashboard.data.local.LessonEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.remote.CourseDto
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.domain.model.ProgressCalculator
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.domain.repository.RefreshOutcome
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class CourseRepositoryImpl(
    private val api: CourseApi,
    private val dao: CourseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> =
        dao.observeCoursesWithLessons().map { rows -> rows.map { it.toDomain() } }

    override fun observeCourse(courseId: Int): Flow<Course?> =
        dao.observeCourseWithLessons(courseId).map { it?.toDomain() }

    override suspend fun refreshCourses(): RefreshOutcome = withContext(ioDispatcher) {
        val dtos = try {
            api.getCourses()
        } catch (e: IOException) {
            return@withContext RefreshOutcome.Offline
        } catch (e: HttpException) {
            return@withContext RefreshOutcome.Failed("Server error ${e.code()}")
        } catch (e: SerializationException) {
            return@withContext RefreshOutcome.Failed("Unexpected response from server")
        }

        // Merge rule: server owns titles and the catalogue; the device owns completion.
        // A lesson the user completed locally stays completed after every refresh.
        val locallyCompleted = dao.getCompletedLessonIds().toHashSet()
        val courses = dtos.map { it.toEntity() }
        val lessons = dtos.flatMap { it.toLessonEntities(locallyCompleted) }
        dao.replaceCatalogue(courses, lessons)
        RefreshOutcome.Success
    }

    override suspend fun markLessonCompleted(lessonId: Int) = withContext(ioDispatcher) {
        dao.markLessonCompleted(lessonId)
    }
}

private fun CourseDto.toEntity() = CourseEntity(id = id, title = title, instructor = instructor)

/**
 * First time we see a course, seed completion from the server's progress % so the derived
 * value matches what the API reported. After that, local completion always wins.
 */
private fun CourseDto.toLessonEntities(locallyCompleted: Set<Int>): List<LessonEntity> {
    val seededCount = ProgressCalculator.seededCompletedCount(progress, lessons.size)
    return lessons.mapIndexed { index, lesson ->
        LessonEntity(
            id = lesson.id,
            courseId = id,
            title = lesson.title,
            position = index,
            isCompleted = lesson.id in locallyCompleted || index < seededCount,
        )
    }
}

private fun CourseWithLessons.toDomain() = Course(
    id = course.id,
    title = course.title,
    instructor = course.instructor,
    lessons = lessons.sortedBy { it.position }.map {
        Lesson(id = it.id, courseId = it.courseId, title = it.title, isCompleted = it.isCompleted)
    },
)
