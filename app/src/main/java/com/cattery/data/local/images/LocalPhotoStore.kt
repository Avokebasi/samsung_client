package com.cattery.data.local.images

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.cattery.presentation.util.ImageDecoder
import java.io.File
import java.util.UUID

class LocalPhotoStore(
    private val context: Context,
) {
    private val directory: File
        get() = File(context.filesDir, "pet_photos").apply { mkdirs() }

    fun saveFromUri(uri: Uri, encoder: ImageDataUrlEncoder): String? {
        val jpeg = encoder.encodeToJpegBytes(uri, maxSidePx = 512) ?: return null
        return saveJpegBytes(jpeg)
    }

    fun saveFromDataUrl(dataUrl: String): String? {
        val bytes = ImageDecoder.decodeDataUrlBytes(dataUrl) ?: return null
        return saveJpegBytes(bytes)
    }

    fun persistRemotePhotos(urls: List<String>): List<String> = urls.mapNotNull { persistRemotePhoto(it) }

    fun persistRemotePhoto(url: String): String? {
        return when {
            url.startsWith("data:image") -> saveFromDataUrl(url)
            url.startsWith("file://") -> {
                val path = Uri.parse(url).path ?: return null
                val file = File(path)
                if (!file.exists()) return null
                saveJpegBytes(file.readBytes())
            }
            url.startsWith("http") -> url
            File(url).exists() -> url
            else -> null
        }
    }

    fun toDataUrl(stored: String): String? = when {
        stored.startsWith("data:image") -> stored
        stored.startsWith("http") -> stored
        stored.startsWith("file://") -> {
            val path = Uri.parse(stored).path ?: return null
            fileToDataUrl(File(path))
        }
        else -> fileToDataUrl(File(stored))
    }

    fun toDataUrls(stored: List<String>): List<String> = stored.mapNotNull { toDataUrl(it) }

    fun clearAll() {
        directory.listFiles()?.forEach { it.delete() }
    }

    private fun saveJpegBytes(bytes: ByteArray): String {
        val file = File(directory, "photo_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg")
        file.writeBytes(bytes)
        return file.absolutePath
    }

    private fun fileToDataUrl(file: File): String? {
        if (!file.exists()) return null
        val base64 = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64"
    }
}
