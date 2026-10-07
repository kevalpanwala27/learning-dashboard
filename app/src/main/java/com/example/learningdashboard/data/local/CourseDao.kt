package com.example.learningdashboard.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Transaction
    @Query("SELECT * FROM courses ORDER BY id")
    fun observeCoursesWithLessons(): Flow<List<CourseWithLessons>>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun observeCourseWithLessons(courseId: Int): Flow<CourseWithLessons?>

    @Query("SELECT id FROM lessons WHERE isCompleted = 1")
    suspend fun getCompletedLessonIds(): List<Int>

    @Upsert
    suspend fun upsertCourses(courses: List<CourseEntity>)

    @Upsert
    suspend fun upsertLessons(lessons: List<LessonEntity>)

    @Query("DELETE FROM courses WHERE id NOT IN (:keepIds)")
    suspend fun deleteCoursesNotIn(keepIds: List<Int>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: Int)

    /** Atomically replaces the catalogue with the server's version. Lesson rows carry their own completion flag. */
    @Transaction
    suspend fun replaceCatalogue(courses: List<CourseEntity>, lessons: List<LessonEntity>) {
        deleteCoursesNotIn(courses.map { it.id })
        upsertCourses(courses)
        upsertLessons(lessons)
    }
}
