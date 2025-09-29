package com.example.citiway.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalDate

@Composable
fun CompletedJourneyCardWithButton(
    route: String,
    date: LocalDate,
    durationMin: Int,
    icon: @Composable (Modifier) -> Unit
) {
    Row {
        CompletedJourneyCard(route, date, durationMin)
        Spacer(modifier = Modifier.weight(0.05f))
        RoundIconButton(icon, Modifier.align(Alignment.CenterVertically))
    }
}
