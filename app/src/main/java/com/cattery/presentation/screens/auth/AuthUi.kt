package com.cattery.presentation.screens.auth

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.TextPrimary

@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BluePrimary,
    focusedLabelColor = BluePrimary,
    cursorColor = BluePrimary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
)
