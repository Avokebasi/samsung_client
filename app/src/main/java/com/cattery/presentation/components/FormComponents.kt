package com.cattery.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.TextPrimary
import com.cattery.presentation.theme.TextSecondary

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            focusedLabelColor = BluePrimary,
            cursorColor = BluePrimary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            unfocusedLabelColor = TextSecondary,
        ),
    )
}
