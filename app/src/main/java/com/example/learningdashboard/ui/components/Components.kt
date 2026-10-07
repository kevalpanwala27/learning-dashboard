package com.example.learningdashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.R
import com.example.learningdashboard.ui.theme.heroGradient

/** Gradient block used at the top of every screen. Extends under the status bar; content is padded below it. */
@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(heroGradient()),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            content = content,
        )
    }
}

/** Circular progress with a rounded cap. [progress] is 0f..1f. */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 6.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            if (progress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        }
        content()
    }
}

/** Full-width pill button painted with the brand gradient. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.8f),
        ),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier
            .height(52.dp)
            .clip(CircleShape)
            .background(heroGradient()),
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Centered message for empty and error states: icon in a tinted disc, title, body, optional action. */
@Composable
fun StateMessage(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            GradientButton(text = actionLabel, onClick = onAction, modifier = Modifier.width(180.dp))
        }
    }
}

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.dashboard_offline_banner), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
