package com.cattery.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object ImageDecoder {
    fun decodeDataUrlBytes(dataUrl: String): ByteArray? = runCatching {
        val marker = "base64,"
        val index = dataUrl.indexOf(marker)
        if (index < 0) return null
        val base64 = dataUrl.substring(index + marker.length)
        if (base64.isEmpty()) return null
        Base64.decode(base64, Base64.DEFAULT)
    }.getOrNull()

    fun decodeDataUrl(dataUrl: String): Bitmap? = runCatching {
        val bytes = decodeDataUrlBytes(dataUrl) ?: return null
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
