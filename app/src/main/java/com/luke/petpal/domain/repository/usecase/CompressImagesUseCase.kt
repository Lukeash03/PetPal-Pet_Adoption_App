package com.luke.petpal.domain.repository.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class CompressImagesUseCase {
    fun execute(imageStrings: List<String>, context: Context): List<String> {
        return imageStrings.mapNotNull { imageString ->
            val uri = Uri.parse(imageString)
            val compressedUri = compressImage(uri, context)
            compressedUri?.toString()
        }
    }

    private fun compressImage(uri: Uri, context: Context): Uri? {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        val compressedFile =
            File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(compressedFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Compress to 70% quality
        outputStream.flush()
        outputStream.close()
        return Uri.fromFile(compressedFile)
    }

}