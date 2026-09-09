package com.movexa.android.presentation.auth.onboarding

import androidx.compose.ui.graphics.Color
import com.movexa.android.ui.theme.*

data class BlobConfig(val xFrac: Float, val yFrac: Float, val radiusFrac: Float)
data class ShapeConfig(val xFrac: Float, val yFrac: Float)

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val bgColor: Color,
    val accentColor: Color,
    val shapeColor: Color,
    val blobs: List<BlobConfig>,
    val shapes: List<ShapeConfig>
)

val onboardingPages = listOf(
    OnboardingPageData(
        title = "Track every\nmovement",
        subtitle = "GPS-powered routes, live pace,\ndistance and heart rate — all in one place.",
        bgColor = DarkBackground,
        accentColor = MovexaBlue,
        shapeColor = MovexaBlueDark,
        blobs = listOf(
            BlobConfig(0.15f, 0.22f, 0.48f),
            BlobConfig(0.82f, 0.58f, 0.36f),
            BlobConfig(0.50f, 0.88f, 0.28f)
        ),
        shapes = listOf(
            ShapeConfig(0.78f, 0.20f),
            ShapeConfig(0.12f, 0.62f),
            ShapeConfig(0.58f, 0.50f)
        )
    ),
    OnboardingPageData(
        title = "Build your\nroutine",
        subtitle = "Set daily goals, log workouts and\nwatch your weekly progress grow.",
        bgColor = Color(0xFF070F14), // Keeping a slight variation for depth
        accentColor = MovexaGreen,
        shapeColor = Color(0xFF0A5C3A),
        blobs = listOf(
            BlobConfig(0.72f, 0.18f, 0.42f),
            BlobConfig(0.18f, 0.68f, 0.38f),
            BlobConfig(0.52f, 0.44f, 0.26f)
        ),
        shapes = listOf(
            ShapeConfig(0.22f, 0.16f),
            ShapeConfig(0.82f, 0.72f),
            ShapeConfig(0.44f, 0.58f)
        )
    ),
    OnboardingPageData(
        title = "Own your\nprogress",
        subtitle = "See your history, break personal\nrecords and stay motivated every day.",
        bgColor = Color(0xFF0D0D12), // Matching Splash background
        accentColor = MovexaOrange,
        shapeColor = Color(0xFF7A1F00),
        blobs = listOf(
            BlobConfig(0.28f, 0.28f, 0.44f),
            BlobConfig(0.76f, 0.64f, 0.36f),
            BlobConfig(0.08f, 0.80f, 0.30f)
        ),
        shapes = listOf(
            ShapeConfig(0.82f, 0.22f),
            ShapeConfig(0.24f, 0.74f),
            ShapeConfig(0.56f, 0.44f)
        )
    )
)