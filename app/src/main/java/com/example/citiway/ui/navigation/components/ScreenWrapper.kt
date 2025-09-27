package com.example.citiway.ui.navigation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ScreenWrapper(
    navController: NavController,
    drawerState: DrawerState,
    showBottomBar: Boolean,
    content: @Composable (NavController, PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navController, drawerState) },
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        content(navController, paddingValues)
    }
}
