package com.example.learningdashboard.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>,
) {
    val lessonCount: Int get() = lessons.size
    val completedCount: Int get() = lessons.count { it.isCompleted }

    /** Always derived from lessons so the list and details screens can never disagree. */
    val progressPercent: Int get() = ProgressCalculator.percent(completedCount, lessonCount)
}

data class Lesson(
    val id: Int,
    val courseId: Int,
    val title: String,
    val isCompleted: Boolean,
)
