package com.cattery.data.local.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max

class ImageDataUrlEncoder(
    private val context: Context,
) {
    fun encode(uri: Uri, maxSidePx: Int = 512, quality: Int = 85): String? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        } ?: return null

        val sampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxSidePx)
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        val scaled = scaleBitmap(bitmap, maxSidePx)
        if (scaled !== bitmap) {
            bitmap.recycle()
        }

        ByteArrayOutputStream().use { output ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
            scaled.recycle()
            val base64 = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64"
        }
    }.getOrNull()

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
