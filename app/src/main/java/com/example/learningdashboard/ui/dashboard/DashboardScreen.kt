package com.example.learningdashboard.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.ui.components.AppCard
import com.example.learningdashboard.ui.components.InverseButton
import com.example.learningdashboard.ui.components.OfflineBanner
import com.example.learningdashboard.ui.components.OutlinePill
import com.example.learningdashboard.ui.components.PercentBadge
import com.example.learningdashboard.ui.components.ProgressGauge
import com.example.learningdashboard.ui.components.StatBlock
import com.example.learningdashboard.ui.components.StateMessage
import com.example.learningdashboard.ui.components.ThinProgressBar

@Composable
fun DashboardScreen(
    onOpenCourse: (courseId: Int) -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshFailedTemplate = stringResource(R.string.dashboard_refresh_failed)

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(refreshFailedTemplate.format(message))
        }
    }

    DashboardContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onLogout = viewModel::logout,
        onOpenCourse = onOpenCourse,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenCourse: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val isRefreshing = (state as? DashboardUiState.Success)?.isRefreshing == true
        val pullState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header") { DashboardHeader(onLogout) }

                when (state) {
                    DashboardUiState.Loading -> item(key = "loading") {
                        Box(Modifier.fillMaxWidth().padding(vertical = 96.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    DashboardUiState.Empty -> item(key = "empty") {
                        StateMessage(
                            icon = Icons.Outlined.School,
                            title = stringResource(R.string.dashboard_empty_title),
                            body = stringResource(R.string.dashboard_empty),
                        )
                    }

                    is DashboardUiState.Error -> item(key = "error") {
                        StateMessage(
                            icon = Icons.Filled.CloudOff,
                            title = stringResource(
                                if (state.isOffline) R.string.dashboard_offline_empty_title else R.string.dashboard_error_title,
                            ),
                            body = if (state.isOffline) stringResource(R.string.dashboard_offline_empty) else state.message,
                            actionLabel = stringResource(R.string.dashboard_retry),
                            onAction = onRefresh,
                        )
                    }

                    is DashboardUiState.Success -> {
                        item(key = "overview") { OverviewCard(state.overall) }
                        if (state.isOffline) {
                            item(key = "offline-banner") { OfflineBanner() }
                        }
                        item(key = "courses-title") {
                            Text(
                                stringResource(R.string.dashboard_courses_header),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                            )
                        }
                        items(state.courses, key = { it.id }) { course ->
                            CourseCard(course = course, onOpen = { onOpenCourse(course.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.dashboard_greeting),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineLarge)
        }
        OutlinePill(text = stringResource(R.string.dashboard_logout), onClick = onLogout, icon = Icons.AutoMirrored.Filled.Logout)
    }
}

@Composable
private fun OverviewCard(overall: OverallProgress) {
    AppCard {
        Text(stringResource(R.string.dashboard_your_progress), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ProgressGauge(progress = overall.percent / 100f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.course_progress, overall.percent), style = MaterialTheme.typography.displaySmall)
                    Text(
                        stringResource(R.string.dashboard_complete),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatBlock(value = overall.courseCount.toString(), label = stringResource(R.string.stat_courses))
            StatBlock(value = overall.completedLessons.toString(), label = stringResource(R.string.stat_lessons_done))
            StatBlock(
                value = (overall.totalLessons - overall.completedLessons).toString(),
                label = stringResource(R.string.stat_remaining),
            )
        }
    }
}

@Composable
private fun CourseCard(course: Course, onOpen: () -> Unit) {
    AppCard(onClick = onOpen) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.course_by, course.instructor).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(course.title, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            PercentBadge(course.progressPercent)
        }
        Spacer(Modifier.height(16.dp))
        ThinProgressBar(progress = course.progressPercent / 100f)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.course_lessons_done, course.completedCount, course.lessonCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            InverseButton(text = stringResource(R.string.course_continue), onClick = onOpen)
        }
    }
}
