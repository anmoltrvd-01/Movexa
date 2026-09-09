package com.movexa.android.presentation.auth.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.ui.theme.MovexaSpacing
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPage(pageIndex)
        }

        // Top Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(MovexaSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateToLogin) {
                Text("Login", color = colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onFinished) {
                Text("Skip", color = colorScheme.onSurfaceVariant)
            }
        }

        // Bottom Navigation
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .padding(horizontal = MovexaSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { i ->
                    val isSelected = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colorScheme.primary else colorScheme.outlineVariant)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.completeOnboarding()
                        onFinished()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    if (pagerState.currentPage < onboardingPages.size - 1) "Next" else "Get Started",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(index: Int) {
    val page = onboardingPages[index]
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MovexaSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(page.accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when(index) {
                    0 -> Icons.Rounded.TrackChanges
                    1 -> Icons.Rounded.Build
                    else -> Icons.Rounded.EmojiEvents
                },
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = page.accentColor
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = colorScheme.onSurfaceVariant
        )
    }
}
