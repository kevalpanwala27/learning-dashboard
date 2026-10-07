package com.example.learningdashboard.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val isLoggedIn: StateFlow<Boolean>

    suspend fun login(email: String, password: String): LoginResult

    fun logout()
}

sealed interface LoginResult {
    data object Success : LoginResult
    data class Failure(val message: String) : LoginResult
}
