package com.cattery.presentation.util

import android.net.Uri
import java.io.File

fun resolvePhotoModel(photoUrl: String): Any = when {
    photoUrl.startsWith("data:image") -> photoUrl
    photoUrl.startsWith("file://") -> photoUrl
    photoUrl.startsWith("http") -> photoUrl
    File(photoUrl).exists() -> File(photoUrl)
    else -> Uri.parse(photoUrl)
}
