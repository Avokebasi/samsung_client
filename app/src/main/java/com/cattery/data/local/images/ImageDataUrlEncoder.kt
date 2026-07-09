package com.cattery.data.local.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

class ImageDataUrlEncoder(
    private val context: Context,
) {
    fun encode(uri: Uri, maxSidePx: Int = 512, quality: Int = 85): String? {
        val bytes = readBytes(uri) ?: return null
        if (bytes.isEmpty()) return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val sampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxSidePx)
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return null

        val scaled = scaleBitmap(bitmap, maxSidePx)
        if (scaled !== bitmap) {
            bitmap.recycle()
        }

        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
        scaled.recycle()

        val base64 = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64"
    }

    private fun readBytes(uri: Uri): ByteArray? {
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            val file = File(path)
            if (!file.exists()) return null
            return file.readBytes()
        }
        return context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    private fun calculateSampleSize(width: Int, height: Int, maxSide: Int): Int {
        var size = 1
        while (width / size > maxSide * 2 || height / size > maxSide * 2) {
            size *= 2
        }
        return size
    }

    private fun scaleBitmap(source: Bitmap, maxSide: Int): Bitmap {
        val width = source.width
        val height = source.height
        val largest = max(width, height)
        if (largest <= maxSide) return source
        val ratio = maxSide.toFloat() / largest
        val targetWidth = (width * ratio).toInt().coerceAtLeast(1)
        val targetHeight = (height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
}
