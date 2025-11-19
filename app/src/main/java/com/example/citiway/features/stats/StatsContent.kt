package com.example.citiway.features.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace

@Composable
fun StatsContent(
    paddingValues: PaddingValues,
    state: StatsState,
    onRefresh: () -> Unit
) {
    // Stats page specific colors
    val statsCardBackground = if (MaterialTheme.colorScheme.background == Color(0xFF122140)) {
        Color(0xFF1A2F4F) // Slightly lighter than dark background
    } else {
        Color.White
    }

    val accentGradientStart = MaterialTheme.colorScheme.primary
    val accentGradientEnd = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace(10)

        // Header with refresh button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title("Monthly Stats")
                VerticalSpace(8)
                Text(
                    text = state.currentMonth,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onRefresh,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh stats",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        VerticalSpace(30)

        // Show loading or error states
        when {
            state.isLoading -> {
                LoadingState()
            }
            state.error != null -> {
                ErrorState(error = state.error, onRetry = onRefresh)
            }
            state.totalSpent == 0.0 && state.walkingDistanceMeters == 0 -> {
                EmptyState()
            }
            else -> {
                // Main content
                TotalSpendingCard(
                    totalSpent = state.totalSpent,
                    monthlyBudget = state.monthlyBudget,
                    cardBackground = statsCardBackground
                )

                VerticalSpace(30)

                // Transport mode breakdown
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Spending Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Bus - Primary blue
                    TransportModeCard(
                        icon = Icons.Default.DirectionsBus,
                        label = "MyCiti Bus",
                        amount = state.busSpent,
                        distance = state.busDistance,
                        color = MaterialTheme.colorScheme.primary,
                        cardBackground = statsCardBackground
                    )

                    // Train - Gold/Orange
                    TransportModeCard(
                        icon = Icons.Default.Train,
                        label = "Metrorail Train",
                        amount = state.trainSpent,
                        distance = state.trainDistance,
                        color = MaterialTheme.colorScheme.secondary,
                        cardBackground = statsCardBackground
                    )

                    // Walking - Complementary color
                    val walkingColor = if (MaterialTheme.colorScheme.background == Color(0xFF122140)) {
                        Color(0xFF4ECDC4) // Teal for dark mode
                    } else {
                        Color(0xFF14A38B) // Green-teal for light mode
                    }

                    TransportModeCard(
                        icon = Icons.Default.DirectionsWalk,
                        label = "Walking",
                        amount = null,
                        distanceMeters = state.walkingDistanceMeters,
                        color = walkingColor,
                        cardBackground = statsCardBackground
                    )
                }

                VerticalSpace(20)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Loading your stats...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    val emptyStateBackground = if (MaterialTheme.colorScheme.background == Color(0xFF122140)) {
        Color(0xFF1A2F4F)
    } else {
        Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = emptyStateBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 64.sp
                )
                Text(
                    text = "No trips this month yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Start tracking your journeys to see statistics here",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun TotalSpendingCard(
    totalSpent: Double,
    monthlyBudget: Double,
    cardBackground: Color
) {
    val progress = (totalSpent / monthlyBudget).toFloat().coerceIn(0f, 1f)
    val isOverBudget = totalSpent > monthlyBudget

    val progressColor = if (isOverBudget) {
        Color(0xFFE74C3C) // Red for over budget
    } else {
        Brush.sweepGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.primary
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            VerticalSpace(24)

            // Circular progress indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Background circle
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = if (MaterialTheme.colorScheme.background == Color(0xFF122140)) {
                        Color(0xFF0D1B33)
                    } else {
                        Color(0xFFF0F0F0)
                    },
                    strokeWidth = 14.dp,
                )

                // Progress circle
                if (isOverBudget) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFE74C3C),
                        strokeWidth = 14.dp,
                    )
                } else {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 14.dp,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "R${String.format("%.2f", totalSpent)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) {
                            Color(0xFFE74C3C)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontSize = 36.sp
                    )
                    Text(
                        text = "of R${String.format("%.2f", monthlyBudget)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            VerticalSpace(24)

            // Budget remaining
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (MaterialTheme.colorScheme.background == Color(0xFF122140)) {
                    Color(0xFF0D1B33)
                } else {
                    Color(0xFFF8F9FA)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOverBudget) "Over Budget" else "Remaining",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "R${String.format("%.2f", kotlin.math.abs(monthlyBudget - totalSpent))}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) {
                            Color(0xFFE74C3C)
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransportModeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    amount: Double? = null,
    distance: Double? = null,
    distanceMeters: Int? = null,
    color: androidx.compose.ui.graphics.Color,
    cardBackground: Color
) {
    val hasData = (amount != null && amount > 0) ||
            (distance != null && distance > 0) ||
            (distanceMeters != null && distanceMeters > 0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Icon + Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icon container
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = color.copy(alpha = 0.12f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            distanceMeters != null && distanceMeters > 0 -> "$distanceMeters m"
                            distance != null && distance > 0 -> "${String.format("%.1f", distance)} km"
                            else -> "No trips yet"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Right side: Amount
            Text(
                text = when {
                    amount != null && amount > 0 -> "R${String.format("%.2f", amount)}"
                    else -> "R0.00"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (hasData) color else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}