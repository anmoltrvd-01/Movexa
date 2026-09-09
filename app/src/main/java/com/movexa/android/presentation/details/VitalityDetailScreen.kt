package com.movexa.android.presentation.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
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
import com.movexa.android.domain.model.TodayStats
import com.movexa.android.presentation.home.BioOrbCanvas
import com.movexa.android.ui.theme.MovexaSpacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VitalityDetailScreen(
    stats: TodayStats,
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
                title = { Text("Vitality", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
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

            // Large Hero Orb
            with(sharedTransitionScope) {
                BioOrbCanvas(
                    modifier = Modifier
                        .size(240.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "vitality_orb"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    stepsProgress = stats.stepsProgress,
                    calsProgress = stats.caloriesBurned / 2000f,
                    actProgress = stats.activeMinutes / 60f
                )
            }

            Spacer(Modifier.height(MovexaSpacing.xl))

            with(sharedTransitionScope) {
                Text(
                    "${(stats.stepsProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.primary,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "vitality_number"),
                        animatedVisibilityScope
                    )
                )
            }

            Text(
                "DAILY GOAL",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(MovexaSpacing.xxl))

            // Detailed Stat Cards
            Column(
                modifier = Modifier.padding(horizontal = MovexaSpacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(MovexaSpacing.md)
            ) {
                DetailStatCard(
                    "Steps",
                    stats.steps.toString(),
                    stats.stepsProgress,
                    Icons.AutoMirrored.Rounded.DirectionsWalk,
                    colorScheme.primary
                )
                DetailStatCard(
                    "Calories",
                    "${stats.caloriesBurned} kcal",
                    stats.caloriesBurned / 2000f,
                    Icons.Rounded.Whatshot,
                    colorScheme.error
                )
                DetailStatCard(
                    "Active",
                    "${stats.activeMinutes} min",
                    stats.activeMinutes / 60f,
                    Icons.Rounded.Timer,
                    colorScheme.secondary
                )
            }
            
            Spacer(Modifier.height(MovexaSpacing.xxl))
        }
    }
}

@Composable
private fun DetailStatCard(
    label: String,
    value: String,
    progress: Float,
    icon: ImageVector,
    color: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(MovexaSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color)
            }
            Spacer(Modifier.width(MovexaSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(MovexaSpacing.xs))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
