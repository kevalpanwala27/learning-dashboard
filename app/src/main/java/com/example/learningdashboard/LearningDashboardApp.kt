package com.example.learningdashboard

import android.app.Application
import com.example.learningdashboard.di.AppContainer

class LearningDashboardApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
