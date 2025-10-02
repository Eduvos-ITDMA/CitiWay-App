package com.example.citiway.core.ui.components

/** This file defines the BottomNavigationBar composable, which is responsible for
 * displaying the main navigation options at the bottom of the screen. It integrates
 * with the Navigation Graph defined in graphs/NavGraph.kt to handle item selection
 * and navigation state.
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.citiway.core.navigation.routes.BottomNavScreen

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Bottom navigation items
    val items = listOf(
        BottomNavScreen.Home, BottomNavScreen.Plan, BottomNavScreen.Journey, BottomNavScreen.Trips
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            BottomNavItem(item, currentDestination, navController)
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    item: BottomNavScreen, currentDestination: NavDestination?, navController: NavController
) {
    val isSelected = currentDestination?.hierarchy?.any {
        it.route == item.route
    } == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable {
                // This code ensures we pop the current tab's back stack before navigating to the
                // specified screen BUT save the state so it can be restored when needed
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .weight(1f)) {

        // Nav item icon
        Image(
            painter = painterResource(id = item.iconResId),
            contentDescription = item.title,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp),
            colorFilter = if (isSelected) {
                ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )

        // Nav item label
        Text(
            text = item.title, fontSize = 14.sp, fontWeight = if (isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )

        // Nav item indicator (stripe below selected item)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }

    }
}