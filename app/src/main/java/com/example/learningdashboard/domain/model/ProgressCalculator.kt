package com.example.learningdashboard.domain.model

import kotlin.math.roundToInt

object ProgressCalculator {

    /** Whole-number percentage of completed lessons, clamped to 0..100. Zero lessons means 0%. */
    fun percent(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        return (completed * 100.0 / total).roundToInt().coerceIn(0, 100)
    }

    /**
     * Inverse of [percent]: how many of [total] lessons should start as completed
     * so the derived progress matches a server-provided [percent] on first load.
     */
    fun seededCompletedCount(percent: Int, total: Int): Int {
        if (total <= 0) return 0
        return (percent.coerceIn(0, 100) * total / 100.0).roundToInt().coerceIn(0, total)
    }
}
