package com.example.citiway.features.schedules


// HomeContent.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import android.content.Intent

import androidx.compose.ui.platform.LocalContext
import  androidx.navigation.NavController
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.example.citiway.core.ui.theme.CitiWayTheme


@Composable
fun SchedulesContent(
    paddingValues: PaddingValues,
    navController: NavController,
//    onToggleFavourite: (id: String) -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MyCitiSchedules()
//        Space(1f)
}}
@Composable
fun MyCitiSchedules() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(end = 12.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, "https://www.myciti.org.za/en/timetables/route-stop-station-timetables/".toUri())
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "See MyCiTi Schedules:",
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
        Text(
            text = "View Schedules on MyCiti Site:",
            color = MaterialTheme.colorScheme.inversePrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Thin
        )
    }
}
@Preview(showBackground = true, name = "Schedules Screen")
@Composable
fun SchedulesScreenPreview() {
    CitiWayTheme {
        SchedulesContent(
            paddingValues = PaddingValues(),
            navController = rememberNavController(), // Or callbacks like onScheduleSelected
        )
    }
}
