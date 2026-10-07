package com.example.learningdashboard.di

import android.content.Context

/**
 * Hand-wired dependency graph. One instance lives in [com.example.learningdashboard.LearningDashboardApp].
 *
 * Kept manual on purpose: for three screens it is easier to read and explain than Hilt,
 * and every dependency is visible in one file. Swapping to Hilt later is mechanical.
 */
class AppContainer(private val context: Context)
