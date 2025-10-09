package com.example.citiway.core.utils
// In a file like NavControllerDebugger.kt

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController

/** This file contains a composable function for logging the current navigation back stack.
 * It's useful for debugging navigation issues by providing a real-time view of the
 * routes present in the navigation controller's back stack.
 */

@SuppressLint("RestrictedApi")
@Composable
fun NavStackLogger(navController: NavController, tag: String = "NavDebug") {
    // Collect the entire back stack list as a state
    val routes = navController
        .currentBackStack.collectAsState().value.joinToString(", ") { it.destination.route.toString() }

    Log.d("BackStackLog", "BackStack: $routes")
}
