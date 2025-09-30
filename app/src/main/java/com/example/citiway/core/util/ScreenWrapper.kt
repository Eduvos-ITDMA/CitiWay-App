package com.example.citiway.core.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.citiway.core.ui.components.BottomNavigationBar
import com.example.citiway.core.ui.components.TopBar

@Composable
fun ScreenWrapper(
    navController: NavController,
    drawerState: DrawerState,
    showBottomBar: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navController, drawerState) },
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        content(paddingValues)
    }
}
