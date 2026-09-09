package com.movexa.android.presentation.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movexa.android.ui.theme.MovexaSpacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SleepDetailScreen(
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
                title = { Text("Rest & Recovery", style = MaterialTheme.typography.titleLarge) },
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
                        .size(140.dp)
                        .background(colorScheme.secondary.copy(alpha = 0.1f), CircleShape)
                        .sharedBounds(
                            rememberSharedContentState("sleep_icon_box"),
                            animatedVisibilityScope
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Bedtime,
                        null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            with(sharedTransitionScope) {
                Text(
                    "92",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState("sleep_number"),
                        animatedVisibilityScope
                    )
                )
            }
            Text("SLEEP SCORE", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(MovexaSpacing.xxl))

            SleepGraphCard()

            Spacer(Modifier.height(MovexaSpacing.md))

            Row(Modifier.padding(horizontal = MovexaSpacing.screenPadding), horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)) {
                SleepStatCard("REM", "2h 14m", Icons.Rounded.AutoAwesome, colorScheme.secondary, Modifier.weight(1f))
                SleepStatCard("Deep", "1h 45m", Icons.Rounded.BrightnessLow, colorScheme.primary, Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(MovexaSpacing.xxl))
        }
    }
}

@Composable
private fun SleepGraphCard() {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            Text("SLEEP STAGES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(MovexaSpacing.md))

            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val stages = listOf(0.8f, 0.4f, 0.2f, 0.6f, 0.9f, 0.5f, 0.3f, 0.7f)
                val stageW = size.width / stages.size
                
                stages.forEachIndexed { i, s ->
                    val h = s * size.height
                    drawRect(
                        color = colorScheme.secondary.copy(alpha = 0.6f),
                        topLeft = androidx.compose.ui.geometry.Offset(i * stageW + 4.dp.toPx(), size.height - h),
                        size = androidx.compose.ui.geometry.Size(stageW - 8.dp.toPx(), h)
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
