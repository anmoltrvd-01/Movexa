package com.movexa.android.presentation.auth.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.ui.theme.MovexaMotion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onFinished: (SplashViewModel.AuthState) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    // Stage machine
    var stage by remember { mutableStateOf(SplashStage.LogoIn) }

    // Spring-based Animatables (§5)
    val logoAlpha   = remember { Animatable(0f) }
    val logoScale   = remember { Animatable(0.9f) }
    val gapWidth    = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Logo Entrance with Spring (§5.2)
        stage = SplashStage.LogoIn
        coroutineScope {
            launch { logoAlpha.animateTo(1f, MovexaMotion.expressiveEffects) }
            launch { logoScale.animateTo(1f, MovexaMotion.expressiveSpatial) }
        }
        
        // Wait for auth state
        val finalState = snapshotFlow { viewModel.authState.value }.filterNotNull().first()
        delay(800.milliseconds)

        // Step 2: Curtain Reveal (§5.2)
        stage = SplashStage.Reveal
        gapWidth.animateTo(1f, MovexaMotion.expressiveSpatial)

        onFinished(finalState)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // Organic branding content (§3)
        SplashBrandingContent(
            alpha = logoAlpha.value,
            scale = logoScale.value
        )

        // The "Curtain" Overlay (revealing the app)
        if (gapWidth.value < 0.99f) {
            CurtainOverlay(gapFrac = gapWidth.value, curtainColor = colorScheme.surface)
        }
    }
}

@Composable
private fun SplashBrandingContent(alpha: Float, scale: Float = 1f) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
    ) {
        Text(
            text = "MOVEXA",
            style = MaterialTheme.typography.displayLarge,
            color = colorScheme.primary,
            fontWeight = FontWeight.Black,
            letterSpacing = 8.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "TRACK · TRAIN · TRANSFORM",
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.secondary,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CurtainOverlay(gapFrac: Float, curtainColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val halfGap = (gapFrac * w / 2f).coerceIn(0f, w / 2f)

        // Left curtain
        drawRect(color = curtainColor, topLeft = Offset.Zero, size = Size(w / 2f - halfGap, h))
        // Right curtain
        drawRect(color = curtainColor, topLeft = Offset(w / 2f + halfGap, 0f), size = Size(w / 2f - halfGap, h))
    }
}

private enum class SplashStage { LogoIn, Reveal }
