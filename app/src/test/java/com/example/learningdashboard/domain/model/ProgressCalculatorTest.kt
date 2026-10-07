package com.example.learningdashboard.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun `zero lessons is zero percent, never a division error`() {
        assertEquals(0, ProgressCalculator.percent(completed = 0, total = 0))
    }

    @Test
    fun `percent is rounded to the nearest whole number`() {
        assertEquals(33, ProgressCalculator.percent(completed = 1, total = 3))
        assertEquals(67, ProgressCalculator.percent(completed = 2, total = 3))
        assertEquals(65, ProgressCalculator.percent(completed = 13, total = 20))
        assertEquals(100, ProgressCalculator.percent(completed = 16, total = 16))
    }

    @Test
    fun `percent is clamped to 0 to 100 even with bad input`() {
        assertEquals(100, ProgressCalculator.percent(completed = 25, total = 20))
        assertEquals(0, ProgressCalculator.percent(completed = -3, total = 20))
    }

    @Test
    fun `seeding from the server percent lands within one lesson of that percent`() {
        // The sample courses from the brief. Note 40% of 16 lessons is 6.4 lessons: no whole
        // number of lessons reproduces it exactly, which is exactly why progress is derived
        // from lessons rather than trusted from the server. The guarantee is "within one lesson".
        val samples = listOf(65 to 20, 40 to 16, 25 to 28, 0 to 12, 100 to 7)
        for ((serverPercent, lessonCount) in samples) {
            val seeded = ProgressCalculator.seededCompletedCount(serverPercent, lessonCount)
            val derived = ProgressCalculator.percent(seeded, lessonCount)
            val oneLessonInPercent = 100.0 / lessonCount
            assertTrue(
                "server=$serverPercent% of $lessonCount lessons, derived=$derived%",
                kotlin.math.abs(derived - serverPercent) <= oneLessonInPercent,
            )
        }
        // And where it is exactly representable, it is exact.
        assertEquals(13, ProgressCalculator.seededCompletedCount(65, 20))
        assertEquals(7, ProgressCalculator.seededCompletedCount(25, 28))
    }

    @Test
    fun `course progress is derived from its lessons`() {
        val lessons = (1..4).map { Lesson(id = it, courseId = 1, title = "L$it", isCompleted = it <= 3) }
        val course = Course(id = 1, title = "T", instructor = "I", lessons = lessons)

        assertEquals(75, course.progressPercent)
        assertEquals(100, course.copy(lessons = lessons.map { it.copy(isCompleted = true) }).progressPercent)
    }
}
