package com.movexa.android.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

// ── Screen Definitions ────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Activity : Screen("activity")
    object History  : Screen("history")
    object Profile  : Screen("profile")
}

// ── Bottom Navigation Items ───────────────────────────────────────────────────
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Activity,
        label = "Activity",
        selectedIcon = Icons.AutoMirrored.Filled.DirectionsRun,
        unselectedIcon = Icons.AutoMirrored.Outlined.DirectionsRun
    ),
    BottomNavItem(
        screen = Screen.History,
        label = "History",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    ),
    BottomNavItem(
        screen = Screen.Profile,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    ),
)
