package com.example.citiway.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.citiway.core.utils.mockPlacesActions
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.google.android.libraries.places.api.model.AutocompletePrediction

/**
 * A custom text input field designed for location searching, featuring an inline action icon.
 *
// * This composable provides a styled text field, typically used for entering
 * location queries. It includes a placeholder and an icon slot on the right side
 * for actions.
 *
 * @param modifier An optional [Modifier] to be applied to the `LocationSearchField` itself.
 * @param icon A composable lambda that defines the icon to be displayed on the right side of
 *             the text field. This lambda receives a [Modifier] that should be used to define
 *             an click action on the icon button. It is also intended that this composable
 *             specify its own `imageVector` on its Icon composable`.
 * @param onSelectPrediction The function to execute when a prediction from the dropdown is selected
 * @param placesState An instance of [PlacesState]
 * @param placesActions An instance of [PlacesActions]
 * @param initialValue The initial text to be displayed in the text field. Defaults to an empty string.
 * @param placeholder The placeholder text to be displayed when the text field is empty.
 *                    Defaults to an empty string.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchField(
    modifier: Modifier = Modifier,
    icon: @Composable (Modifier) -> Unit,
    onSelectPrediction: ((AutocompletePrediction) -> Unit)? = null,
    placesState: PlacesState,
    placesActions: PlacesActions,
    initialValue: String = "",
    placeholder: String = ""
) {
    // State variables
    val predictions = placesState.predictions
    var showPredictions by remember { mutableStateOf(predictions.isNotEmpty()) }
    val expanded = showPredictions && predictions.isNotEmpty()

    val textFieldValue by remember(placesState.searchText) {
        val text = placesState.searchText.ifEmpty { initialValue }
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        )
    }
    ExposedDropdownMenuBox(
        expanded = showPredictions,
        onExpandedChange = { isExpanded -> showPredictions = isExpanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue ->
                val query = textFieldValue.text
                if (query.length > 2){
                    showPredictions = true
                }
                placesActions.onSearchPlaces(query)
            },
            placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyLarge) },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            ),
            textStyle = MaterialTheme.typography.titleSmall,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    // ======== Icon and Divider ========
                    VerticalDivider(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalSpace(8)
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                        icon(Modifier.fillMaxHeight(0.6f))
                    }
                    HorizontalSpace(8)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    placesActions.onSearchPlaces(textFieldValue.text)
                }),
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
        )

        /*
        * Search suggestions dropdown card that appears below the search field
        * when suggestions are available.
        */
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { showPredictions = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(scrollState)
                ) {
                    predictions.forEach { prediction ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (onSelectPrediction != null) {
                                        onSelectPrediction(prediction)
                                    } else {
                                        placesActions.onSelectPlace(prediction)
                                    }
                                }
                                .padding(8.dp)) {
                            Text(
                                text = prediction.getPrimaryText(null).toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Show simplified secondary text - removes postal codes for cleaner display
                            val secondaryText = prediction.getSecondaryText(null).toString()
                            val cleanText = secondaryText.split(",").take(2)
                                .joinToString(", ") // Only take first 2 parts
                            Text(
                                text = cleanText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        if (prediction != predictions.last()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.1f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocationSearchFieldPreview() {
    LocationSearchField(icon = {}, placesState = PlacesState(), placesActions = mockPlacesActions)
}