package com.movexa.android.core

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.movexa.android.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.movexa.android.presentation.activity.ActivityScreen
import com.movexa.android.presentation.auth.login.LoginScreen
import com.movexa.android.presentation.auth.onboarding.OnboardingScreen
import com.movexa.android.presentation.auth.signup.SignUpScreen
import com.movexa.android.presentation.auth.splash.SplashScreen
import com.movexa.android.presentation.auth.splash.SplashViewModel
import com.movexa.android.presentation.auth.otp.OtpScreen
import com.movexa.android.presentation.details.*
import com.movexa.android.presentation.history.HistoryScreen
import com.movexa.android.presentation.home.HomeScreen
import com.movexa.android.presentation.home.HomeViewModel
import com.movexa.android.presentation.notifications.NotificationsScreen
import com.movexa.android.presentation.profile.ProfileScreen
import com.movexa.android.ui.theme.*

// ── Auth route constants ──────────────────────────────────────────────────────
private object AuthRoutes {
    const val SPLASH      = "splash"
    const val ONBOARDING  = "onboarding"
    const val LOGIN       = "login"
    const val SIGN_UP     = "signup"
    const val MAIN        = "main"
}

// ── Shared Transition Constants ──────────────────────────────────────────────
object DetailedRoutes {
    const val VITALITY = "vitality_detail"
    const val NOTIFICATIONS = "notifications"
    const val HEART = "heart_detail"
    const val SLEEP = "sleep_detail"
    const val STRESS = "stress_detail"
    const val OTP = "otp"
}

@Composable
fun MovexaNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AuthRoutes.SPLASH
    ) {
        composable(AuthRoutes.SPLASH) {
            SplashScreen(
                onFinished = { authState ->
                    val destination = when (authState) {
                        is SplashViewModel.AuthState.Authenticated -> AuthRoutes.MAIN
                        is SplashViewModel.AuthState.NotAuthenticated -> AuthRoutes.LOGIN
                        is SplashViewModel.AuthState.FirstTime -> AuthRoutes.ONBOARDING
                    }
                    navController.navigate(destination) {
                        popUpTo(AuthRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthRoutes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AuthRoutes.MAIN) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(AuthRoutes.SIGN_UP)
                },
                onNavigateToOtp = { email ->
                    navController.navigate("${DetailedRoutes.OTP}/$email")
                }
            )
        }

        composable(
            route = "${DetailedRoutes.OTP}/{email}",
            arguments = listOf(navArgument("email") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpScreen(
                email = email,
                onVerifySuccess = {
                    navController.navigate(AuthRoutes.MAIN) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(AuthRoutes.MAIN) {
                        popUpTo(AuthRoutes.SIGN_UP) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AuthRoutes.MAIN) {
            MainAdaptiveScreen(onLogout = {
                navController.navigate(AuthRoutes.LOGIN) {
                    popUpTo(AuthRoutes.MAIN) { inclusive = true }
                }
            })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainAdaptiveScreen(onLogout: () -> Unit) {
    val innerNavController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val stats by homeViewModel.stats.collectAsState()
    
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomNavItems.forEach { item ->
                item(
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        if (currentRoute != item.screen.route) {
                            innerNavController.navigate(item.screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { 
                        Icon(
                            if (currentRoute == item.screen.route) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) }
                )
            }
        },
        layoutType = NavigationSuiteType.None
    ) {
        Scaffold(
            bottomBar = {
                MovexaBottomNav(navController = innerNavController)
            }
        ) { innerPadding ->
            SharedTransitionLayout {
                NavHost(
                    navController = innerNavController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(
                        route = Screen.Home.route,
                        enterTransition = {
                            fadeIn(emphasizedDecelerateTween()) + scaleIn(
                                initialScale = 0.92f,
                                animationSpec = emphasizedDecelerateTween()
                            )
                        },
                        exitTransition = {
                            fadeOut(emphasizedTween(DurationShort)) + scaleOut(
                                targetScale = 0.92f,
                                animationSpec = emphasizedTween(DurationShort)
                            )
                        }
                    ) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onNavigateToVitality = { innerNavController.navigate(DetailedRoutes.VITALITY) },
                            onNavigateToCalendar = { innerNavController.navigate(Screen.History.route) },
                            onNavigateToNotifications = { innerNavController.navigate(DetailedRoutes.NOTIFICATIONS) },
                            onNavigateToProfile = { innerNavController.navigate(Screen.Profile.route) },
                            onSeeAllActivities = { innerNavController.navigate(Screen.History.route) },
                            onStartActivity = { innerNavController.navigate(Screen.Activity.route) },
                            onNavigateToHeart = { innerNavController.navigate(DetailedRoutes.HEART) },
                            onNavigateToSleep = { innerNavController.navigate(DetailedRoutes.SLEEP) },
                            onNavigateToStress = { innerNavController.navigate(DetailedRoutes.STRESS) }
                        )
                    }
                    composable(
                        route = DetailedRoutes.VITALITY,
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = emphasizedDecelerateTween(DurationMedium)
                            ) + fadeIn(emphasizedDecelerateTween(DurationMedium))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = emphasizedTween(DurationShort)
                            ) + fadeOut(emphasizedTween(DurationShort))
                        }
                    ) {
                        VitalityDetailScreen(
                            stats = stats,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onBack = { innerNavController.popBackStack() }
                        )
                    }
                    composable(
                        route = DetailedRoutes.HEART,
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = emphasizedDecelerateTween(DurationMedium)
                            ) + fadeIn(emphasizedDecelerateTween(DurationMedium))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = emphasizedTween(DurationShort)
                            ) + fadeOut(emphasizedTween(DurationShort))
                        }
                    ) {
                        HeartDetailScreen(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onBack = { innerNavController.popBackStack() }
                        )
                    }
                    composable(
                        route = DetailedRoutes.SLEEP,
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = emphasizedDecelerateTween(DurationMedium)
                            ) + fadeIn(emphasizedDecelerateTween(DurationMedium))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = emphasizedTween(DurationShort)
                            ) + fadeOut(emphasizedTween(DurationShort))
                        }
                    ) {
                        SleepDetailScreen(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onBack = { innerNavController.popBackStack() }
                        )
                    }
                    composable(
                        route = DetailedRoutes.STRESS,
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = emphasizedDecelerateTween(DurationMedium)
                            ) + fadeIn(emphasizedDecelerateTween(DurationMedium))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = emphasizedTween(DurationShort)
                            ) + fadeOut(emphasizedTween(DurationShort))
                        }
                    ) {
                        StressDetailScreen(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable,
                            onBack = { innerNavController.popBackStack() }
                        )
                    }
                    composable(DetailedRoutes.NOTIFICATIONS) {
                        NotificationsScreen(onBack = { innerNavController.popBackStack() })
                    }
                    composable(Screen.Activity.route) {
                        ActivityScreen()
                    }
                    composable(Screen.History.route) {
                        HistoryScreen()
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(onLogout = onLogout)
                    }
                }
            }
        }
    }
}
