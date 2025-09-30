package com.example.citiway.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Adds a vertical space of a specified height in dp.
 * @param dp The height of the spacer in density-independent pixels.
 */
@Composable
fun VerticalSpace(dp: Int) {
   Spacer(modifier = Modifier.height(dp.dp))
}

/**
 * Adds a horizontal space of a specified width in dp.
 * @param dp The width of the spacer in density-independent pixels.
 */
@Composable
fun HorizontalSpace(dp: Int) {
   Spacer(modifier = Modifier.width(dp.dp))
}

/**
 * Adds a flexible spacer within a [RowScope] that takes up a given weight.
 * @param weight The proportion of available horizontal space this spacer should occupy.
 */
@Composable
fun RowScope.Space(weight: Float){
   Spacer(modifier = Modifier.weight(weight))
}

/**
 * Adds a flexible spacer within a [ColumnScope] that takes up a given weight.
 * @param weight The proportion of available vertical space this spacer should occupy.
 */
@Composable
fun ColumnScope.Space(weight: Float){
   Spacer(modifier = Modifier.weight(weight))
}

