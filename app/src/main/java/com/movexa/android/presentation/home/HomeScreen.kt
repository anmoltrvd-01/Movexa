package com.movexa.android.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.domain.model.ActivitySummary
import com.movexa.android.domain.model.DayActivity
import com.movexa.android.domain.model.TodayStats
import com.movexa.android.ui.theme.MovexaMotion
import com.movexa.android.ui.theme.MovexaSpacing
import kotlin.math.min

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToVitality: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSeeAllActivities: () -> Unit,
    onStartActivity: () -> Unit,
    onNavigateToHeart: () -> Unit,
    onNavigateToSleep: () -> Unit,
    onNavigateToStress: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val weekly by viewModel.weekly.collectAsState()
    val recent by viewModel.recent.collectAsState()
    val hasPermissions by viewModel.hasPermissions.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { _: Set<String> -> }

    if (showPermissionRationale) {
        HealthSyncRationaleDialog(
            onDismiss = { showPermissionRationale = false },
            onConfirm = {
                showPermissionRationale = false
                permissionLauncher.launch(
                    setOf(
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.StepsRecord::class),
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.DistanceRecord::class),
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.TotalCaloriesBurnedRecord::class),
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.ExerciseSessionRecord::class),
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.HeartRateRecord::class),
                        androidx.health.connect.client.permission.HealthPermission
                            .getReadPermission(androidx.health.connect.client.records.SleepSessionRecord::class)
                    )
                )
            }
        )
    }

    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartActivity,
                text = { Text("Start Activity", style = MaterialTheme.typography.labelLarge) },
                icon = { Icon(Icons.Rounded.Add, null) },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = CircleShape
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primaryContainer.copy(alpha = 0.20f),
                            colorScheme.tertiaryContainer.copy(alpha = 0.10f),
                            colorScheme.background
                        )
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                item {
                    HomeHeader(
                        name = userName,
                        onProfileClick = onNavigateToProfile,
                        onNotificationsClick = onNavigateToNotifications
                    )
                }

                if (!hasPermissions) {
                    item {
                        SyncDataCard { showPermissionRationale = true }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(MovexaSpacing.lg))
                    VitalityOrbCard(
                        stats = stats,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onClick = onNavigateToVitality
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(MovexaSpacing.md))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MovexaSpacing.screenPadding),
                        horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)
                    ) {
                        MiniScoreCard(
                            title = "HEART",
                            value = "72",
                            unit = "BPM",
                            icon = Icons.Rounded.Favorite,
                            color = colorScheme.error,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            iconKey = "heart_icon_box",
                            numberKey = "heart_number",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToHeart
                        )
                        MiniScoreCard(
                            title = "SLEEP",
                            value = "92",
                            unit = "SCORE",
                            icon = Icons.Rounded.Bedtime,
                            color = colorScheme.secondary,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            iconKey = "sleep_icon_box",
                            numberKey = "sleep_number",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSleep
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(MovexaSpacing.lg))
                    GoalCalendarSection(weekly, onClick = onNavigateToCalendar)
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MovexaSpacing.screenPadding, vertical = MovexaSpacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Activities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )
                        TextButton(onClick = onSeeAllActivities) { 
                            Text("See All", color = colorScheme.primary) 
                        }
                    }
                }

                items(items = recent) { activity: ActivitySummary ->
                    ActivityRow(activity)
                    Spacer(Modifier.height(MovexaSpacing.sm))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun VitalityOrbCard(
    stats: TodayStats,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MovexaSpacing.screenPadding)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.extraLarge,
        color = colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(MovexaSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            with(sharedTransitionScope) {
                BioOrbCanvas(
                    modifier = Modifier
                        .size(120.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "vitality_orb"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    stepsProgress = stats.stepsProgress,
                    calsProgress = min(1f, stats.caloriesBurned / 2000f),
                    actProgress = min(1f, stats.activeMinutes / 60f)
                )
            }

            Spacer(modifier = Modifier.width(MovexaSpacing.lg))

            Column {
                with(sharedTransitionScope) {
                    Text(
                        "VITALITY",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedBounds(
                            rememberSharedContentState(key = "vitality_label"),
                            animatedVisibilityScope
                        )
                    )
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
                    "You're crushing it today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MiniScoreCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    iconKey: String,
    numberKey: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Using quickSnap for micro-interactions (§5.1)
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = MovexaMotion.quickSnap,
        label = "mini_card_scale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape)
                        .sharedBounds(
                            rememberSharedContentState(iconKey),
                            animatedVisibilityScope
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(MovexaSpacing.md))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.Bottom) {
                with(sharedTransitionScope) {
                    Text(
                        value,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        modifier = Modifier.sharedBounds(
                            rememberSharedContentState(numberKey),
                            animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                    )
                }
                Spacer(modifier = Modifier.width(MovexaSpacing.xs))
                Text(
                    unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivitySummary) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MovexaSpacing.screenPadding)
            .clickable { },
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tonalElevation = 0.dp
    ) {
        Row(Modifier.padding(MovexaSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(activity.type.emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(MovexaSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    activity.type.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    activity.dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${"%.1f".format(activity.distanceKm)} km",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.primary
                )
                Text(
                    activity.pace,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    name: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MovexaSpacing.screenPadding, vertical = MovexaSpacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Keep moving,", style = MaterialTheme.typography.bodyLarge, color = colorScheme.onSurfaceVariant)
            Text(name, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = colorScheme.onSurface)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.background(colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Outlined.Notifications, "Notifications", tint = colorScheme.onSurface)
            }
            Spacer(Modifier.width(MovexaSpacing.md))
            Surface(
                modifier = Modifier.size(48.dp).clickable(onClick = onProfileClick),
                shape = CircleShape,
                color = colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        name.firstOrNull()?.toString() ?: "",
                        color = colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncDataCard(onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MovexaSpacing.screenPadding, vertical = MovexaSpacing.sm)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = colorScheme.primaryContainer.copy(alpha = 0.8f)
    ) {
        Row(Modifier.padding(MovexaSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Sync, null, tint = colorScheme.onPrimary)
            }
            Spacer(Modifier.width(MovexaSpacing.md))
            Column(Modifier.weight(1f)) {
                Text("Sync Fitness Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onPrimaryContainer)
                Text("Link your Google Fit data.", style = MaterialTheme.typography.bodySmall, color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            }
            // Using the KeyboardArrowRight (§2.3/§8)
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = colorScheme.primary)
        }
    }
}

@Composable
private fun GoalCalendarSection(weeklyData: List<DayActivity>, onClick: () -> Unit) {
    val listState = rememberLazyListState()
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Your Streak",
            modifier = Modifier.padding(horizontal = MovexaSpacing.lg),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(MovexaSpacing.md))
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = MovexaSpacing.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(weeklyData) { day ->
                CalendarDayCard(day, onClick = onClick)
            }
        }
    }
}

@Composable
private fun CalendarDayCard(day: DayActivity, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val isToday = day.isToday
    val backgroundColor = if (isToday) colorScheme.primary else colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val contentColor = if (isToday) colorScheme.onPrimary else colorScheme.onSurface
    
    Surface(
        modifier = Modifier
            .width(64.dp)
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = MovexaSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(day.day, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.7f))
            Text(
                if (day.steps >= 1000) "${"%.1f".format(day.steps / 1000f)}k" else if (day.steps > 0) "${day.steps}" else "--",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = contentColor
            )
            if (day.steps >= 10_000) {
                Icon(Icons.Rounded.CheckCircle, null, tint = contentColor, modifier = Modifier.size(16.dp))
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun BioOrbCanvas(modifier: Modifier, stepsProgress: Float, calsProgress: Float, actProgress: Float) {
    val colorScheme = MaterialTheme.colorScheme
    val trackColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)

    val animatedSteps by animateFloatAsState(stepsProgress, MovexaMotion.expressiveSpatial, label = "steps")
    val animatedCals by animateFloatAsState(calsProgress, MovexaMotion.expressiveSpatial, label = "cals")
    val animatedAct by animateFloatAsState(actProgress, MovexaMotion.expressiveSpatial, label = "act")

    Canvas(modifier = modifier.padding(MovexaSpacing.xs)) {
        val strokeWidth = size.minDimension * 0.12f
        val spacing = strokeWidth * 1.3f

        drawCircle(trackColor, radius = size.minDimension / 2, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        drawCircle(trackColor, radius = size.minDimension / 2 - spacing, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        drawCircle(trackColor, radius = size.minDimension / 2 - spacing * 2, style = Stroke(strokeWidth, cap = StrokeCap.Round))

        drawArc(color = colorScheme.primary, startAngle = -90f, sweepAngle = 360f * animatedSteps, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        drawArc(color = colorScheme.error, startAngle = -90f, sweepAngle = 360f * animatedCals, useCenter = false, topLeft = Offset(spacing, spacing), size = Size(size.width - spacing * 2, size.height - spacing * 2), style = Stroke(strokeWidth, cap = StrokeCap.Round))
        drawArc(color = colorScheme.secondary, startAngle = -90f, sweepAngle = 360f * animatedAct, useCenter = false, topLeft = Offset(spacing * 2, spacing * 2), size = Size(size.width - spacing * 4, size.height - spacing * 4), style = Stroke(strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun HealthSyncRationaleDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sync with Movexa", style = MaterialTheme.typography.headlineMedium) },
        text = { Text("To give you the full picture of your fitness, Movexa can sync with Google Fit data.", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = { Button(onClick = onConfirm, shape = MaterialTheme.shapes.medium) { Text("Link Google Fit") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Later") } },
        shape = MaterialTheme.shapes.large,
        containerColor = colorScheme.surfaceContainerHigh
    )
}

@Composable
fun WeeklyChartCard(weeklyData: List<DayActivity>) {
    if (weeklyData.isEmpty()) return
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
        shape = MaterialTheme.shapes.large,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            Text("Weekly Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(MovexaSpacing.md))
            WeeklyBars(weeklyData)
        }
    }
}

@Composable
private fun WeeklyBars(data: List<DayActivity>) {
    val colorScheme = MaterialTheme.colorScheme
    val maxSteps = data.maxOfOrNull { it.steps }?.takeIf { it > 0 } ?: 1L

    Row(modifier = Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
        data.forEach { day ->
            val fraction = (day.steps.toFloat() / maxSteps.toFloat()).coerceIn(0f, 1f)
            val animatedFraction by animateFloatAsState(fraction, MovexaMotion.expressiveSpatial, label = "bar_${day.day}")

            Box(
                modifier = Modifier
                    .width(24.dp)
                    .fillMaxHeight(animatedFraction.coerceAtLeast(0.05f))
                    .clip(MaterialTheme.shapes.small)
                    .background(if (day.isToday) colorScheme.primary else colorScheme.secondary.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun QuickStatsRow(stats: TodayStats) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)
    ) {
        StatChip("🔥", "${stats.caloriesBurned}", "kcal", colorScheme.error.copy(alpha = 0.1f), Modifier.weight(1f))
        StatChip("📍", "%.1f".format(stats.distanceKm), "km", colorScheme.primary.copy(alpha = 0.1f), Modifier.weight(1f))
        StatChip("⏱", "${stats.activeMinutes}", "min", colorScheme.secondary.copy(alpha = 0.1f), Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(emoji: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(Modifier.padding(MovexaSpacing.md), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(MovexaSpacing.sm))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
