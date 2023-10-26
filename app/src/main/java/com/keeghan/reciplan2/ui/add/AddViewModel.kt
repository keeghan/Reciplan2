package com.keeghan.reciplan2.ui.add

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class AddViewModel : ViewModel() {


    //retrieve tempImage and compress it
    fun compressImage(imageUri: Uri, compressedImage: File) {
        try {
            BitmapFactory.decodeFile(imageUri.encodedPath)
                ?.compress(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else Bitmap.CompressFormat.WEBP, 100, FileOutputStream(compressedImage)
                )
            File(imageUri.path!!).delete()
        } catch (e: Exception) {
            Log.i("====>", "onCreateView: ${e.message}")
        }
    }
}