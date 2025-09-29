package com.example.citiway.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpace(dp: Int) {
   Spacer(modifier = Modifier.height(dp.dp))
}

@Composable
fun HorizontalSpace(dp: Int) {
   Spacer(modifier = Modifier.width(dp.dp))
}

@Composable
fun RowScope.Space(weight: Float){
   Spacer(modifier = Modifier.weight(weight))
}
@Composable
fun ColumnScope.Space(weight: Float){
   Spacer(modifier = Modifier.weight(weight))
}
