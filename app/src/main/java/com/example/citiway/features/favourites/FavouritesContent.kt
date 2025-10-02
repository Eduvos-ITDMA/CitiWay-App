package com.example.citiway.features.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Data class representing a favorite route
 *
 * In the future, this will be populated from:
 * - API calls to get user's saved favorite routes test
 * - Local database (Room) for offline storage
 * - User preferences/settings
 *
 * For now, we're using dummy data to show the UI design
 */
data class FavoriteRoute(
    val from: String,        // Starting location name
    val to: String,          // Destination location name
    val duration: String,    // Estimated travel time
    val location: String = "Cape Town"  // General area/city
)

/**
 * Favourites Screen - Shows user's saved favorite routes
 *
 * This screen demonstrates:
 * - LazyColumn for efficient scrolling of route lists
 * - Material Design 3 theming (using MaterialTheme.colorScheme) - Caleb Setup for us
 * - Card components with proper elevation and colors
 * - Integration with bottom navigation
 *
 * Future features will include:
 * - Pull-to-refresh for syncing with server
 * - Swipe-to-delete functionality
 * - Edit/reorder favorites
 */
@Composable
fun FavouritesContent(navController: NavController, paddingValues: PaddingValues) {
    // TODO: Replace with actual API call or database query
    // TODO: Move initialization logic and data class definition to view model
    // This dummy data shows the expected data structure
    val favoriteRoutes = listOf(
        FavoriteRoute("Milnerton", "Mowbray", "60mins", "Cape Town"),
        FavoriteRoute("Table View", "Cape Town", "45mins", "Cape Town"),
        FavoriteRoute("Kleinmond", "Hermanus", "35mins", "Hermanus")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Title using Material Theme colors instead of hardcoded values, allows us to be dynamic light/darkmode etc.
        Text(
            text = "Favourite Routes:",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, // Uses theme color
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Routes list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favoriteRoutes) { route ->
                FavoriteRouteCard(route = route)
            }
        }
    }
}

/**
 * Individual route card component
 *
 * Uses Material Theme colors for consistency:
 * - MaterialTheme.colorScheme.primary for card background
 * - MaterialTheme.colorScheme.onPrimary for text on primary color
 * - MaterialTheme.colorScheme.secondary for favorite icon background
 *
 * This ensures the app automatically adapts to light/dark themes
 * and uses the colors defined in our Theme.kt file
 */
@Composable
fun FavoriteRouteCard(route: FavoriteRoute) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO: Navigate to route details or start navigation
                // navController.navigate("route_details/${route.id}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary // Uses theme primary color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Route info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${route.from} to ${route.to}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary // Theme color for text on primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onPrimary, // Theme color
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = route.location,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary // Theme color
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = route.duration,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary // Theme color
                    )
                }
            }

            // Orange favorite icon circle - using theme colors
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary, // Theme secondary (orange)
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.onSecondary, // Theme color for text on secondary
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}