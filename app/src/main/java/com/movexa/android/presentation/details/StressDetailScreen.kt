package com.movexa.android.presentation.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movexa.android.ui.theme.MovexaSpacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StressDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100.milliseconds)
        isLoaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stress & Wellness", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background)
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(MovexaSpacing.lg))

            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .sharedBounds(
                            rememberSharedContentState("stress_orb_box"),
                            animatedVisibilityScope
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = colorScheme.outlineVariant.copy(alpha = 0.2f),
                            startAngle = 0f, sweepAngle = 360f, useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(colorScheme.tertiary, colorScheme.primary)
                            ),
                            startAngle = -90f, sweepAngle = 360f * 0.32f, useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        "32",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            Text("RELAXED", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary)
            Text("Your stress levels are low today.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(MovexaSpacing.xxl))

            // Stress History Card
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
                shape = MaterialTheme.shapes.medium,
                color = colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column(Modifier.padding(MovexaSpacing.md)) {
                    Text("DAILY TREND", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(MovexaSpacing.md))
                    
                    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        val points = listOf(0.4f, 0.3f, 0.5f, 0.8f, 0.4f, 0.2f, 0.3f)
                        val stepX = size.width / (points.size - 1)
                        val path = androidx.compose.ui.graphics.Path()
                        
                        points.forEachIndexed { i, p ->
                            val x = i * stepX
                            val y = size.height - (p * size.height)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        
                        drawPath(path, colorScheme.primary, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
            
            Spacer(Modifier.height(MovexaSpacing.xxl))
        }
    }
}
