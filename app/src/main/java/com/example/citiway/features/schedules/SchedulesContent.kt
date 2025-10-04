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
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
@Composable
fun SchedulesContent(
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace(10)
        HeaderSection()
        VerticalSpace(100)
        MyCitiSchedules()
        VerticalSpace(50)
        PrasaSchedules()
    }
}
@Composable
fun MyCitiSchedules() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(end = 12.dp)
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://www.myciti.org.za/en/timetables/route-stop-station-timetables/".toUri()
                )
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start // Corrected here
        ) {
            Text(
                text = "See MyCiti Schedules: \uD83D\uDE8F",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold ,
                fontSize = 20.sp,
            )
            Text(

                text = "View Schedules on MyCiti Site:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
            )
        }

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Title("Let's find your Transport Schedules⏰")
        VerticalSpace(4)

    }
}
@Composable
fun PrasaSchedules() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(end = 12.dp)
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://cttrains.co.za/".toUri()
                )
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start // Corrected here
        ) {
            Text(
                text = "See Prasa Schedules: \uD83D\uDE89",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold ,
                fontSize = 20.sp,
            )
            Text(

                text = "View Schedules on cttrains.co.za:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
            )
        }

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}
