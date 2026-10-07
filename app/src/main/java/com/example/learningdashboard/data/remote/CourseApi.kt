package com.example.learningdashboard.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface CourseApi {
    @GET("courses.json")
    suspend fun getCourses(): List<CourseDto>
}

@Serializable
data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    /** Server-side progress. Used only to seed initial lesson completion on first load. */
    val progress: Int,
    val lessons: List<LessonDto>,
)

@Serializable
data class LessonDto(
    val id: Int,
    val title: String,
)
