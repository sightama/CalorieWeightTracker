package com.example.calorieweighttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorieweighttracker.ui.screens.GraphScreen
import com.example.calorieweighttracker.ui.screens.MainScreen
import com.example.calorieweighttracker.ui.theme.CalorieWeightTrackerTheme
import com.example.calorieweighttracker.ui.viewmodels.MainViewModel

/**
 * Main activity - single activity architecture
 * Uses Jetpack Navigation for screen navigation
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieWeightTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalorieWeightTrackerApp()
                }
            }
        }
    }
}

/**
 * Main app composable with navigation
 */
@Composable
fun CalorieWeightTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            // Create a single instance of MainViewModel for swipe support
            val mainViewModel: MainViewModel = viewModel()

            // Wrap MainScreen with swipe gesture detection
            SwipeableMainScreen(
                onNavigateToGraph = { navController.navigate("graph") },
                viewModel = mainViewModel
            )
        }

        composable("graph") {
            GraphScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Main screen with horizontal swipe gesture support
 */
@Composable
fun SwipeableMainScreen(
    onNavigateToGraph: () -> Unit,
    viewModel: MainViewModel
) {
    var dragAmount by remember { mutableStateOf(0f) }

    MainScreen(
        onNavigateToGraph = onNavigateToGraph,
        viewModel = viewModel,
        modifier = Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    // Determine swipe direction based on drag amount
                    when {
                        dragAmount > 50 -> {
                            // Swipe right - go to previous day
                            viewModel.navigateToPreviousDay()
                        }
                        dragAmount < -50 && viewModel.canNavigateToNext() -> {
                            // Swipe left - go to next day (if allowed)
                            viewModel.navigateToNextDay()
                        }
                    }
                    dragAmount = 0f
                }
            ) { _, dragDistance ->
                dragAmount += dragDistance
            }
        }
    )
}