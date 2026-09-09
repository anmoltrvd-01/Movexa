package com.movexa.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.movexa.android.core.MovexaNavGraph
import com.movexa.android.ui.theme.MovexaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovexaTheme {
                val navController = rememberNavController()
                MovexaNavGraph(navController = navController)
            }
        }
    }
}
