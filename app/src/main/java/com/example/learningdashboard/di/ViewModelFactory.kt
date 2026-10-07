package com.example.learningdashboard.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.learningdashboard.LearningDashboardApp
import com.example.learningdashboard.ui.dashboard.DashboardViewModel
import com.example.learningdashboard.ui.details.CourseDetailsViewModel
import com.example.learningdashboard.ui.login.LoginViewModel

/** Builds every ViewModel from the [AppContainer]; the only place UI meets the dependency graph. */
val AppViewModelFactory = viewModelFactory {
    initializer { LoginViewModel(container().authRepository) }
    initializer { DashboardViewModel(container().courseRepository, container().authRepository) }
    initializer { CourseDetailsViewModel(createSavedStateHandle(), container().courseRepository) }
}

private fun CreationExtras.container(): AppContainer =
    (checkNotNull(this[APPLICATION_KEY]) as LearningDashboardApp).container
