package com.depi.bookdiscovery.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * A reusable dialog to confirm the removal of a book from favorites.
 *
 * @param showDialog Whether the dialog should be shown.
 * @param onConfirm Callback for when the user confirms the removal.
 * @param onDismiss Callback for when the user dismisses the dialog.
 * @param bookTitle The title of the book to be removed.
 */
@Composable
fun ConfirmUnfavoriteDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    bookTitle: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Remove from Favorites") },
            text = { Text("Are you sure you want to remove \"$bookTitle\" from your favorites?") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
