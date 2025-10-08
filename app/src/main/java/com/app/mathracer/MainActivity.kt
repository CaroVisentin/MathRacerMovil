package com.app.mathracer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.app.mathracer.ui.navigation.MathRacerNavGraph
import com.app.mathracer.ui.theme.MathRacerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathRacerTheme {
                val navController = rememberNavController()
                MathRacerNavGraph(navController = navController)
            }
        }
    }
}
