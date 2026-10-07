package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    /** Local database is the single source of truth; the UI only ever observes this. */
    fun observeCourses(): Flow<List<Course>>

    fun observeCourse(courseId: Int): Flow<Course?>

    /** Fetches from the network and merges into the local database. Never throws. */
    suspend fun refreshCourses(): RefreshOutcome

    suspend fun markLessonCompleted(lessonId: Int)
}

sealed interface RefreshOutcome {
    data object Success : RefreshOutcome

    /** No connectivity or the host was unreachable. Cached data, if any, is still valid. */
    data object Offline : RefreshOutcome

    /** The server answered but with an error, or returned something we could not parse. */
    data class Failed(val message: String) : RefreshOutcome
}
