package com.example.citiway.ui.screens


// HomeScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.ui.components.CompletedJourneyCard
import com.example.citiway.ui.components.LocationSearchField
import com.example.citiway.ui.components.Space
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.ui.previews.PreviewApp
import java.time.LocalDate

@Composable
fun HomeScreen(navController: NavController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection()
        Space(24)

        DestinationSearchBar()
        Space(24)

        RecentRoutesSection()
        Space(24)

        ScheduleLinksSection()
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Hi, Commuter 👋",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Where would you like to go today?",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DestinationSearchBar() {
    val mapIcon: @Composable (Modifier) -> Unit = { modifier ->
        Icon(
            painterResource(R.drawable.ic_map), contentDescription = "Open Map", modifier = modifier
        )
    }
    LocationSearchField(icon = mapIcon, placeholder = "Where to?")
}

@Composable
fun RecentRoutesSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Routes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PLACEHOLDER: Route Card Composable
        CompletedJourneyCard(
            route = "Claremont to Cape Town",
            date = LocalDate.of(2025, 12, 25),
            durationMin = 50
        )
        Spacer(modifier = Modifier.height(10.dp))
        CompletedJourneyCard(
            route = "Milnerton to Mowbray",
            date = LocalDate.of(2025, 11, 17),
            durationMin = 80
        )
    }
}


@Composable
fun ScheduleLinksSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ScheduleLink(label = "See Prasa Schedules")
        Spacer(modifier = Modifier.height(12.dp))
        ScheduleLink(label = "See MyCiti Schedules")
    }
}

@Composable
fun ScheduleLink(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Placeholder for making the whole row clickable
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview
@Composable
fun CommuterScreenPreview() {
    PreviewApp(Screen.Home.route)
}
