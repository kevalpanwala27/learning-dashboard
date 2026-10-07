package com.example.learningdashboard.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.ui.components.GradientHeader
import com.example.learningdashboard.ui.components.OfflineBanner
import com.example.learningdashboard.ui.components.ProgressRing
import com.example.learningdashboard.ui.components.StateMessage

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
        PullToRefreshBox(
            isRefreshing = (state as? DashboardUiState.Success)?.isRefreshing == true,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "hero") {
                    DashboardHero(overall = (state as? DashboardUiState.Success)?.overall, onLogout = onLogout)
                }

                when (state) {
                    DashboardUiState.Loading -> item(key = "loading") {
                        Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
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
                        if (state.isOffline) {
                            item(key = "offline-banner") {
                                OfflineBanner(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                        items(state.courses, key = { it.id }) { course ->
                            CourseCard(
                                course = course,
                                onOpen = { onOpenCourse(course.id) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHero(overall: OverallProgress?, onLogout: () -> Unit) {
    GradientHeader {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.dashboard_greeting),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(R.string.dashboard_logout), tint = Color.White)
            }
        }
        Spacer(Modifier.height(20.dp))
        Surface(
            color = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(
                    progress = (overall?.percent ?: 0) / 100f,
                    size = 64.dp,
                    strokeWidth = 7.dp,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    progressColor = Color.White,
                ) {
                    Text(
                        stringResource(R.string.course_progress, overall?.percent ?: 0),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(stringResource(R.string.dashboard_overall_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (overall == null) {
                            stringResource(R.string.dashboard_overall_loading)
                        } else {
                            stringResource(
                                R.string.dashboard_overall_summary,
                                overall.courseCount,
                                overall.completedLessons,
                                overall.totalLessons,
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseCard(course: Course, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onOpen),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(progress = course.progressPercent / 100f, size = 60.dp, strokeWidth = 6.dp) {
                    Text(
                        stringResource(R.string.course_progress, course.progressPercent),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(course.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.course_instructor, course.instructor),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = CircleShape,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.course_lessons_done, course.completedCount, course.lessonCount),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onOpen,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(stringResource(R.string.course_continue), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
