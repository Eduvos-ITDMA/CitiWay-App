package com.example.citiway.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.ButtonColors
import androidx.compose.foundation.border
import androidx.compose.ui.text.TextStyle

@Composable
fun ConfirmationDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = "Continue",
    dismissText: String = "No, go back",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    // Style parameters
    dialogShape: Shape = RoundedCornerShape(16.dp),
    dialogBackground: Color = MaterialTheme.colorScheme.surface,
    dialogBorderColor: Color = MaterialTheme.colorScheme.primary,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onSecondary,
    messageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    messageColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.onSecondary
    ),
    dismissButtonColors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = Color.Transparent,

        ),
    dismissButtonBorderColor: Color = MaterialTheme.colorScheme.onSecondary,
    buttonShape: Shape = RoundedCornerShape(6.dp),
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            shape = dialogShape,
            containerColor = dialogBackground,
            modifier = Modifier.border(
                BorderStroke(2.dp, dialogBorderColor),
                shape = dialogShape
            ),
            title = {
                Text(
                    text = title,
                    style = titleTextStyle,
                    color = titleColor
                )
            },
            text = {
                Text(
                    text = message,
                    style = messageTextStyle,
                    color = messageColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = confirmButtonColors,
                    shape = buttonShape,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                        .padding(end = 8.dp)
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = dismissButtonColors,
                    border = BorderStroke(2.dp, dismissButtonBorderColor),
                    shape = buttonShape,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                        .padding(end = 8.dp)
                ) {
                    Text(dismissText)
                }
            }
        )
    }
}

