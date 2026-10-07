package com.example.learningdashboard.di

import android.content.Context
import com.example.learningdashboard.BuildConfig
import com.example.learningdashboard.data.local.AppDatabase
import com.example.learningdashboard.data.local.TokenStore
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.data.repository.CourseRepositoryImpl
import com.example.learningdashboard.data.repository.MockAuthRepository
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.CourseRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Hand-wired dependency graph. One instance lives in [com.example.learningdashboard.LearningDashboardApp].
 *
 * Kept manual on purpose: for three screens it is easier to read and explain than Hilt,
 * and every dependency is visible in one file. Swapping to Hilt later is mechanical.
 */
class AppContainer(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val courseApi: CourseApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CourseApi::class.java)

    private val database = AppDatabase.build(context)

    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi, database.courseDao())

    val authRepository: AuthRepository = MockAuthRepository(TokenStore(context))
}
