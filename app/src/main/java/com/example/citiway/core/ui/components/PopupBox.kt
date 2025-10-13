package com.example.citiway.core.ui.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.ButtonColors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle


//class PopupBox {
//}
@Composable
fun ConfirmationDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = "Continue",
    dismissText: String = "No, go back",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onClose: (() -> Unit)? = null,
    // Style parameters you can add as needed:
    dialogShape: Shape = RoundedCornerShape(16.dp), // Dialog shape styling
    dialogBackground: Color = MaterialTheme.colorScheme.surface, // Dialog background color
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge, // Title style
    titleColor: Color = MaterialTheme.colorScheme.onSurface,         // Title color
    messageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,  // Message style
    messageColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,   // Message color
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary           // Confirm button bg color
    ),
    dismissButtonColors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent                           // Outlined button bg color
    ),
    buttonShape: Shape = RoundedCornerShape(6.dp),                   // Button shapes
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant      // Icon color
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            shape = dialogShape, // Place to style dialog corners
            containerColor = dialogBackground, // Dialog background color (Material3)
            // Custom close ("X") icon in top-right
            icon = if (onClose != null) {
                {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = iconTint // Color of the X icon
                        )
                    }
                }
            } else null,
            title = {
                Text(
                    text = title,
                    style = titleTextStyle, // Title font, weight, size
                    color = titleColor      // Title color
                )
            },
            text = {
                Text(
                    text = message,
                    style = messageTextStyle, // Message font
                    color = messageColor,     // Message color
                    modifier = Modifier.padding(bottom = 12.dp) // Message padding
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = confirmButtonColors, // Button background colors
                    shape = buttonShape ,          // Button corners
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                        .padding(end = 8.dp)      // Button spacing
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = dismissButtonColors, // Outlined button colors
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

