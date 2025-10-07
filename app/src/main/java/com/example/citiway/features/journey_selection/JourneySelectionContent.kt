package com.example.citiway.features.journey_selection


// HomeContent.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace

@Composable
fun JourneySelectionContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title("Select Your Journey")
        VerticalSpace(24)

        SelectedLocationFields()
    }
}

@Composable
fun SelectedLocationFields(modifier: Modifier = Modifier) {
// TODO: Make section with LocationSearchFields and connected column - tricky!!
}
