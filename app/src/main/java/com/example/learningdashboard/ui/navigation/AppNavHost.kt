package com.example.learningdashboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.ui.dashboard.DashboardScreen
import com.example.learningdashboard.ui.details.CourseDetailsScreen
import com.example.learningdashboard.ui.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Dashboard

@Serializable
data class CourseDetails(val courseId: Int)

@Composable
fun AppNavHost(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle()

    // Session state drives the auth boundary: login success and logout both land here,
    // so neither screen needs to know about navigation.
    LaunchedEffect(isLoggedIn) {
        val onLogin = navController.currentDestination?.route?.endsWith(Login::class.simpleName!!) ?: true
        if (isLoggedIn && onLogin) {
            navController.navigate(Dashboard) { popUpTo(Login) { inclusive = true } }
        } else if (!isLoggedIn && !onLogin) {
            navController.navigate(Login) { popUpTo(0) { inclusive = true } }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Dashboard else Login,
    ) {
        composable<Login> {
            LoginScreen()
        }
        composable<Dashboard> {
            DashboardScreen(onOpenCourse = { id -> navController.navigate(CourseDetails(id)) })
        }
        composable<CourseDetails> { entry ->
            CourseDetailsScreen(
                courseId = entry.toRoute<CourseDetails>().courseId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
