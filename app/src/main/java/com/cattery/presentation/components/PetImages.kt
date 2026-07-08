package com.cattery.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cattery.R
import com.cattery.presentation.theme.BlueLight

@Composable
fun UserAvatar(
    avatarUrl: String?,
    localUri: String?,
    onClick: () -> Unit,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val model = localUri ?: avatarUrl
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BlueLight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (model.isNullOrBlank()) {
            Image(
                painter = painterResource(R.drawable.ic_avatar_placeholder),
                contentDescription = null,
                modifier = Modifier.size(size * 0.7f),
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                error = painterResource(R.drawable.ic_avatar_placeholder),
            )
        }
    }
}

@Composable
fun PetPhoto(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 88.dp,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.medium)
            .background(BlueLight),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl.isNullOrBlank()) {
            Image(
                painter = painterResource(R.drawable.ic_pet_placeholder),
                contentDescription = null,
                modifier = Modifier.size(size * 0.65f),
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_pet_placeholder),
                error = painterResource(R.drawable.ic_pet_placeholder),
            )
        }
    }
}
