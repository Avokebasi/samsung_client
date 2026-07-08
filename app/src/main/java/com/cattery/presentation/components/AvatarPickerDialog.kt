package com.cattery.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cattery.R

@Composable
fun AvatarPickerDialog(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.avatar_picker_title)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onGallery()
                },
            ) {
                Text(stringResource(R.string.avatar_from_gallery))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onCamera()
                },
            ) {
                Text(stringResource(R.string.avatar_from_camera))
            }
        },
    )
}
