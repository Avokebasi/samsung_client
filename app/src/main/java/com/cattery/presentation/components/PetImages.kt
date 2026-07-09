package com.cattery.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cattery.R
import com.cattery.presentation.theme.BlueLight
import com.cattery.presentation.util.ImageDecoder
import com.cattery.presentation.util.resolvePhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun UserAvatar(
    avatarUrl: String?,
    localUri: String?,
    onClick: () -> Unit,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val model = localUri?.takeIf { it.isNotBlank() } ?: avatarUrl?.takeIf { it.isNotBlank() }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BlueLight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            model.isNullOrBlank() -> {
                AvatarPlaceholder(size)
            }
            model.startsWith("data:image") -> {
                DataUrlImage(
                    dataUrl = model,
                    modifier = Modifier.fillMaxSize(),
                    placeholderSize = size,
                )
            }
            else -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(resolvePhotoModel(model))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                    error = painterResource(R.drawable.ic_avatar_placeholder),
                )
            }
        }
    }
}

@Composable
fun PetPhoto(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 88.dp,
    expand: Boolean = false,
) {
    val context = LocalContext.current
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .then(
                if (expand) {
                    Modifier.fillMaxWidth().height(size)
                } else {
                    Modifier.size(size)
                },
            )
            .clip(shape)
            .background(BlueLight),
        contentAlignment = Alignment.Center,
    ) {
        when {
            photoUrl.isNullOrBlank() -> {
                PetPlaceholder(size)
            }
            photoUrl.startsWith("data:image") -> {
                DataUrlImage(
                    dataUrl = photoUrl,
                    modifier = Modifier.fillMaxSize(),
                    placeholderSize = size,
                    placeholderRes = R.drawable.ic_pet_placeholder,
                )
            }
            else -> {
                val model = remember(photoUrl) { resolvePhotoModel(photoUrl) }
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(model)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_pet_placeholder),
                    error = painterResource(R.drawable.ic_pet_placeholder),
                )
            }
        }
    }
}

@Composable
private fun DataUrlImage(
    dataUrl: String,
    modifier: Modifier,
    placeholderSize: Dp,
    placeholderRes: Int = R.drawable.ic_pet_placeholder,
) {
    var bitmap by remember(dataUrl) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(dataUrl) {
        bitmap = withContext(Dispatchers.Default) {
            ImageDecoder.decodeDataUrl(dataUrl)?.asImageBitmap()
        }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        Image(
            painter = painterResource(placeholderRes),
            contentDescription = null,
            modifier = Modifier.size(placeholderSize * 0.65f),
        )
    }
}

@Composable
private fun AvatarPlaceholder(size: Dp) {
    Image(
        painter = painterResource(R.drawable.ic_avatar_placeholder),
        contentDescription = null,
        modifier = Modifier.size(size * 0.7f),
    )
}

@Composable
private fun PetPlaceholder(size: Dp) {
    Image(
        painter = painterResource(R.drawable.ic_pet_placeholder),
        contentDescription = null,
        modifier = Modifier.size(size * 0.65f),
    )
}
