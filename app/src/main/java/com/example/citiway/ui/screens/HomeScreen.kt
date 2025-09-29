package com.example.citiway.ui.screens


// HomeScreen.kt
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.example.citiway.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.ui.components.Heading
import com.example.citiway.ui.components.LocationSearchField
import com.example.citiway.ui.components.Space
import com.example.citiway.ui.components.Title
import com.example.citiway.ui.components.VerticalSpace
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
        VerticalSpace(24)

        DestinationSearchBar()
        VerticalSpace(24)

        RecentTripsSection()
        VerticalSpace(24)

        FavouriteTripsSection()
        VerticalSpace(24)

        SchedulesLink(navController)
        Space(1f)
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Title("Hi, Commuter 👋")
        VerticalSpace(4)
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

private val favouriteIcon: @Composable (Modifier) -> Unit = { modifier ->
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = "Add to favourites",
            modifier = modifier.clickable {
                Log.d("Favourite Button", "Button clicked")
            })
}

@Composable
fun RecentTripsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Heading("Recent Routes")

        VerticalSpace(12)

        CompletedJourneyCardWithButton(
            route = "Claremont to Cape Town",
            date = LocalDate.of(2025, 12, 25),
            durationMin = 50,
            icon = favouriteIcon
        )
        VerticalSpace(15)
        CompletedJourneyCardWithButton(
            route = "Milnerton to Mowbray",
            date = LocalDate.of(2025, 11, 17),
            durationMin = 80,
            icon = favouriteIcon
        )
    }
}

@Composable
fun FavouriteTripsSection(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Heading("Favourite Routes")

        VerticalSpace(12)

        CompletedJourneyCardWithButton(
            route = "Claremont to Cape Town",
            date = LocalDate.of(2025, 12, 25),
            durationMin = 50,
            icon = favouriteIcon
        )
    }
}

@Composable
fun SchedulesLink(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(end = 12.dp)
            .clickable{
                navController.navigate(Screen.Schedules.route)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "See MyCiTi and Metrorail Schedules",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Preview
@Composable
fun CommuterScreenPreview() {
    PreviewApp(Screen.Home.route)
}
