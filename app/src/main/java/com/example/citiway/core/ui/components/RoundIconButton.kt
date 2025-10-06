package com.example.citiway.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
/**
 * A custom composable that renders a round button with an icon.
 *
 * This component is designed to display an icon within a circular Card,
 * typically used for actions. The icon itself should be provided as a composable lambda,
 * that specifies the clickable action and the `imageVector` directly within the passed
 * composable.
 *
 * @param icon A composable lambda that defines the icon to be displayed.
 *             This lambda receives a [Modifier] that should be applied to the icon
 *             content for proper sizing within the button. It is intended that
 *             this composable render an `Icon` with its own `imageVector` and
 *             `modifier = Modifier.clickable { /* action */ }`.
 * @param modifier An optional [Modifier] to be applied to the `RoundIconButton` itself.
 */
@Composable
fun RoundIconButton(icon: @Composable (Modifier) -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier.size(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
                icon(Modifier.fillMaxSize(0.7f))
            }
        }
    }
}