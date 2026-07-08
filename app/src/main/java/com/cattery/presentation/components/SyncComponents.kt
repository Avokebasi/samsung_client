package com.cattery.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cattery.R
import com.cattery.presentation.theme.BlueLight
import com.cattery.presentation.theme.TextPrimary

@Composable
fun OfflineBanner(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.offline_banner),
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(BlueLight)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
fun LastSyncLabel(
    formattedTime: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.last_sync, formattedTime),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
