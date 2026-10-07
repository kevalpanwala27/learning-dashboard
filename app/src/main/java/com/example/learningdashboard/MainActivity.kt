package com.example.learningdashboard

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.learningdashboard.ui.navigation.AppNavHost
import com.example.learningdashboard.ui.theme.LearningDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Every screen opens with a gradient header, so status bar icons are always light.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val container = (application as LearningDashboardApp).container
        setContent {
            LearningDashboardTheme {
                AppNavHost(authRepository = container.authRepository)
            }
        }
    }
}
