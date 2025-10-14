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
import com.example.citiway.core.ui.components.ConfirmationDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.ButtonColors
import androidx.compose.foundation.border
import androidx.compose.material3.ChipBorder

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
    dialogShape: Shape = RoundedCornerShape(16.dp),
    dialogBackground: Color = MaterialTheme.colorScheme.surface,
    dialogBorderColor: Color=Color(0xFF109FD6),
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = Color(0xFF0B3B4E),
    messageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    messageColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(

        containerColor = Color(0xFF0B3B4E)
    ),
    dismissButtonColors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = Color(0xFF0B3B4E),
        containerColor = Color.Transparent,

    ),
    dismissButtonBorderColor: Color=Color(0xFF0B3B4E),
    buttonShape:Shape=RoundedCornerShape(6.dp),
//    buttonShape: Shape = RoundedCornerShape(6.dp),
//    iconTint: Color = Color(0xFF0B3B4E),
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
            // Custom close ("X") icon in top-right
//            icon = if (onClose != null) {
//                {
//                    IconButton(onClick = onClose) {
//                        Icon(
//                            Icons.Default.Close,
//                            contentDescription = "Close dialog",
//                            tint = iconTint // Color of the X icon
//                        )
//                    }
//                }
//            } else null,
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
                    shape = buttonShape ,
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

