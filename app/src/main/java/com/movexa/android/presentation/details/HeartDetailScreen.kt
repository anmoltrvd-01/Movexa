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
import androidx.compose.ui.graphics.Path
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
fun HeartDetailScreen(
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
                title = { Text("Heart Health", style = MaterialTheme.typography.titleLarge) },
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

            // Hero Heart Box
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(colorScheme.error.copy(alpha = 0.1f), CircleShape)
                        .sharedBounds(
                            rememberSharedContentState("heart_icon_box"),
                            animatedVisibilityScope
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Favorite,
                        null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(Modifier.height(MovexaSpacing.lg))

            with(sharedTransitionScope) {
                Text(
                    "72",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState("heart_number"),
                        animatedVisibilityScope
                    )
                )
            }
            Text("BPM TODAY", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(MovexaSpacing.xxl))

            LiveEkgCard()

            Spacer(Modifier.height(MovexaSpacing.md))

            HeartZonesCard()
            
            Spacer(Modifier.height(MovexaSpacing.xxl))
        }
    }
}

@Composable
private fun LiveEkgCard() {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            Text("LIVE EKG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colorScheme.error)
            Spacer(Modifier.height(MovexaSpacing.md))
            
            val infiniteTransition = rememberInfiniteTransition(label = "ekg")
            val ekgProgress by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
                label = "ekg_anim"
            )

            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                val path = Path()
                val w = size.width
                val midY = size.height / 2
                val segmentW = w / 10
                
                path.moveTo(0f, midY)
                for (i in 0..10) {
                    val startX = i * segmentW
                    path.lineTo(startX + (segmentW * 0.2f), midY)
                    path.lineTo(startX + (segmentW * 0.3f), midY - 30f)
                    path.lineTo(startX + (segmentW * 0.4f), midY + 40f)
                    path.lineTo(startX + (segmentW * 0.5f), midY - 10f)
                    path.lineTo(startX + (segmentW * 0.6f), midY)
                }

                drawPath(
                    path = path,
                    color = colorScheme.error,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Active scanning line
                val scanX = ekgProgress * w
                drawLine(
                    color = colorScheme.error.copy(alpha = 0.3f),
                    start = androidx.compose.ui.geometry.Offset(scanX, 0f),
                    end = androidx.compose.ui.geometry.Offset(scanX, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun HeartZonesCard() {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = MovexaSpacing.screenPadding),
        shape = MaterialTheme.shapes.medium,
        color = colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(Modifier.padding(MovexaSpacing.md)) {
            Text("INTENSITY ZONES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(MovexaSpacing.md))

            ZoneLegend("Zone 5", "VO2 Max", colorScheme.error)
            ZoneLegend("Zone 4", "Anaerobic", colorScheme.error.copy(alpha = 0.7f))
            ZoneLegend("Zone 3", "Aerobic", colorScheme.primary)
            ZoneLegend("Zone 2", "Fat Burn", colorScheme.primary.copy(alpha = 0.7f))
            ZoneLegend("Zone 1", "Warm Up", colorScheme.outline)
        }
    }
}

@Composable
private fun ZoneLegend(label: String, desc: String, color: Color) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(12.dp).background(color, CircleShape))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
