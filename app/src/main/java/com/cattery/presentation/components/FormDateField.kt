package com.cattery.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cattery.R
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.TextPrimary
import com.cattery.presentation.theme.TextSecondary
import com.cattery.presentation.util.DateFormatter
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDateField(
    isoValue: String,
    onIsoValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    val displayValue = remember(isoValue) {
        if (isoValue.isBlank()) "" else DateFormatter.formatDisplay(isoValue)
    }
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = displayValue,
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { showDialog = true },
        label = { Text(label) },
        readOnly = true,
        singleLine = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = BluePrimary,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            focusedLabelColor = BluePrimary,
            cursorColor = BluePrimary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            unfocusedLabelColor = TextSecondary,
        ),
    )

    if (showDialog) {
        val initialMillis = remember(isoValue) {
            DateFormatter.toEpochMillis(isoValue) ?: System.currentTimeMillis()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val iso = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                            onIsoValueChange(iso)
                        }
                        showDialog = false
                    },
                ) {
                    Text(stringResource(R.string.date_picker_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.date_picker_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
