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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue



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
    onClose: (() -> Unit)? = null
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
            text = { Text(text = message) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp)
                ) {
                    Text(dismissText)
                }
            },
            /* Optionally, add a close icon in the top corner */
            icon = if (onClose != null) {
                { IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close") } }
            } else null,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
