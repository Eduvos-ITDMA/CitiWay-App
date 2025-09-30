package com.example.citiway.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalDate

/**
 * A composable that displays a completed journey's information alongside an action button.
 *
 * This component arranges a [CompletedJourneyCard] and a [RoundIconButton] horizontally
 * in a [Row]. It is designed to present journey details with a directly associated action,
 * such as favouriting or linking to the journey's summary.
 *
 * @param route A string representing the journey's route, passed to [CompletedJourneyCard].
 * @param date The [LocalDate] when the journey was completed, passed to [CompletedJourneyCard].
 * @param durationMin The duration of the journey in minutes, passed to [CompletedJourneyCard].
 * @param icon A composable lambda that defines the icon and action for the [RoundIconButton].
 *             This lambda receives a [Modifier] that should be applied to the icon content.
 *             and specifies `modifier = Modifier.clickable { /* action */ }`. It is also
 *             intended that the icon define its own `imageVector`.
 */
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
