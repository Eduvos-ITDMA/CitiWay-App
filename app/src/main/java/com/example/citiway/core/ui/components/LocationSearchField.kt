package com.example.citiway.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.citiway.features.shared.LocationSelectionActions
import com.example.citiway.features.shared.LocationSelectionState

/**
 * A custom text input field designed for location searching, featuring an inline action icon.
 *
 * This composable provides a styled text field, typically used for entering
 * location queries. It includes a placeholder and an icon slot on the right side
 * for actions.
 *
 * @param icon A composable lambda that defines the icon to be displayed on the right side of
 *             the text field. This lambda receives a [Modifier] that should be used to define
 *             an click action on the icon button. It is also intended that this composable
 *             specify its own `imageVector` on its Icon composable`.
 * @param modifier An optional [Modifier] to be applied to the `LocationSearchField` itself.
 * @param initialValue The initial text to be displayed in the text field. Defaults to an empty string.
 * @param placeholder The placeholder text to be displayed when the text field is empty.
 *                    Defaults to an empty string.
 */
@Composable
fun LocationSearchField(
    icon: @Composable (Modifier) -> Unit,
    state: LocationSelectionState,
    actions: LocationSelectionActions,
    modifier: Modifier = Modifier,
    initialValue: String = "",
    placeholder: String = ""
) {
    // State variables
    val searchText = state.searchText
    val showPredictions = state.showPredictions
    val predictions = state.predictions

    val searchBarHeight: Dp = 60.dp

    Box(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { query ->
                actions.setSearchText(query)
                actions.searchPlaces(query)
            },
            placeholder = {
                Text(
                    text = "Search for your location",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
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
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)){
                    VerticalDivider(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalSpace(8)
                    // Icon on right side
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
                    actions.searchPlaces(searchText)
                }
            ),
            modifier = modifier
                .fillMaxWidth()
                .height(searchBarHeight)
        )

        /*
     * Search suggestions dropdown card that appears below the search field
     * when suggestions are available.
     * `heightIn` limits dropdown size to prevent screen overflow
     */
        if (showPredictions && predictions.isNotEmpty()) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = searchBarHeight + 4.dp)
                    .zIndex(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(predictions) { prediction ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { actions.selectPlace(prediction) }
                                .padding(8.dp)
                        ) {
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

//    OutlinedCard(
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
//        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
//        modifier = modifier
//            .fillMaxWidth()
//            .height(60.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Text Input Placeholder
//            TextField(
//                state = rememberTextFieldState(initialValue),
//                lineLimits = TextFieldLineLimits.SingleLine,
//                placeholder = {
//                    Text(
//                        text = placeholder,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                },
//                colors = TextFieldDefaults.colors(
//                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
//                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.7f),
//                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.6f),
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                ),
//                contentPadding = PaddingValues(0.dp),
//                textStyle = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier
//                    .weight(1f)
//                    .wrapContentHeight()
//            )
//
//            // Divider
//            HorizontalSpace(8)
//            VerticalDivider(
//                modifier = Modifier
//                    .width(2.dp)
//                    .fillMaxHeight(),
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            HorizontalSpace(8)
//
//            // Icon on right side
//            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
//                icon(Modifier.fillMaxHeight())
//            }
//        }
//    }
}