package com.example.citiway.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.citiway.data.local.JourneyOverview
import java.time.LocalDate

/**
 * A composable that displays a completed journey's information alongside an action button.
 *
 * This component arranges a [CompletedJourneyCard] and a [RoundIconButton] horizontally
 * in a [Row]. It is designed to present journey details with a directly associated action,
 * such as favouriting or linking to the journey's summary.
 *
 * @param journey The JourneyOverview object which contains all journey data for display
 * @param icon A composable lambda that defines the icon and action for the [RoundIconButton].
 *             This lambda receives a [Modifier] that should be applied to the icon content.
 *             and specifies `modifier = Modifier.clickable { /* action */ }`. It is also
 *             intended that the icon define its own `imageVector`.
 */
@Composable
fun CompletedJourneyCardWithButton(
    journey: JourneyOverview,
    icon: @Composable (Modifier) -> Unit,
    outlined: Boolean = false,
    onCardClick: (JourneyOverview) -> Unit,
) {
    Row {
        CompletedJourneyCard(journey, onCardClick)
        Spacer(modifier = Modifier.weight(0.05f))
        RoundIconButton(icon, Modifier.align(Alignment.CenterVertically), outlined)
    }
}
