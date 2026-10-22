package com.example.mcmovilcare.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri

fun cargarBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
        val original = context.contentResolver.openInputStream(uri)?.use { flujo ->
            BitmapFactory.decodeStream(flujo, null, opciones)
        } ?: return null

        val grados = try {
            context.contentResolver.openInputStream(uri)?.use { flujo ->
                when (ExifInterface(flujo).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f
        } catch (e: Exception) {
            0f
        }

        if (grados == 0f) {
            original
        } else {
            val matriz = Matrix().apply { postRotate(grados) }
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
        }
    } catch (e: Exception) {
        null
    }
}
