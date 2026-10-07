package com.example.learningdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.learningdashboard.ui.navigation.AppNavHost
import com.example.learningdashboard.ui.theme.LearningDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as LearningDashboardApp).container
        setContent {
            LearningDashboardTheme {
                AppNavHost(authRepository = container.authRepository)
            }
        }
    }
}
