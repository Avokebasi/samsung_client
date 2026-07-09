package com.cattery.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object ImageDecoder {
    fun decodeDataUrl(dataUrl: String): Bitmap? = runCatching {
        val marker = "base64,"
        val index = dataUrl.indexOf(marker)
        if (index < 0) return null
        val base64 = dataUrl.substring(index + marker.length)
        if (base64.isEmpty()) return null
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
