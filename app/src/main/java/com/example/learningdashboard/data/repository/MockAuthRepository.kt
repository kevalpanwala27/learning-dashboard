package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.local.TokenStore
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.LoginResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Stands in for a real auth API. Accepts any well-formed credentials except [BLOCKED_EMAIL],
 * which always fails so the error state can be demonstrated. Issues an opaque session token
 * that is persisted through [TokenStore].
 */
class MockAuthRepository(
    private val tokenStore: TokenStore,
    private val networkDelayMs: Long = 800,
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(tokenStore.read() != null)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    override suspend fun login(email: String, password: String): LoginResult {
        delay(networkDelayMs)
        if (email.trim().equals(BLOCKED_EMAIL, ignoreCase = true)) {
            return LoginResult.Failure("Invalid email or password")
        }
        tokenStore.write(UUID.randomUUID().toString())
        _isLoggedIn.value = true
        return LoginResult.Success
    }

    override fun logout() {
        tokenStore.clear()
        _isLoggedIn.value = false
    }

    companion object {
        const val BLOCKED_EMAIL = "blocked@example.com"
    }
}
