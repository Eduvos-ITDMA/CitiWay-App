package com.example.citiway.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier,
    initialValue: String = "",
    placeholder: String = ""
) {
    OutlinedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Input Placeholder
            TextField(
                state = rememberTextFieldState(initialValue),
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                contentPadding = PaddingValues(0.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            )

            // Divider
            Spacer(modifier = Modifier.width(8.dp))
            VerticalDivider(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Icon on right side
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                icon(Modifier.fillMaxHeight())
            }
        }
    }
}