package com.movexa.android.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

// ── Motion Physics System (§5.1) ─────────────────────────────────────────────

object MovexaMotion {
    // Spatial changes (position, size, shape morph): expressiveSpatial. 
    // Slight overshoot is intentional.
    val expressiveSpatial = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Effects (color, opacity, elevation): expressiveEffects. No bounce.
    val expressiveEffects = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Micro-interactions (button press, chip toggle): quickSnap. Fast response.
    val quickSnap = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )
}

// M3 Expressive spring specs (Legacy naming / helper)
val SpringDefault = MovexaMotion.expressiveSpatial
val SpringBouncy = spring<Float>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow
)
val SpringSnappy = MovexaMotion.quickSnap

// M3 Easing curves
val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

// Duration tokens
const val DurationShort = 200
const val DurationMedium = 350
const val DurationLong = 500

// Tween specs using M3 easing
fun <T> emphasizedTween(duration: Int = DurationMedium) = tween<T>(
    durationMillis = duration,
    easing = EmphasizedEasing
)

fun <T> emphasizedDecelerateTween(duration: Int = DurationMedium) = tween<T>(
    durationMillis = duration,
    easing = EmphasizedDecelerateEasing
)
