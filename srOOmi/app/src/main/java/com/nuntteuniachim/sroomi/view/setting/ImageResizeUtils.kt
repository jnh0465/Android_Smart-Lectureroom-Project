package com.nuntteuniachim.sroomi.view.setting

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object ImageResizeUtils {

    //이미지 너비 변경
    fun resizeFile(file: File, newFile: File, newWidth: Int, isCamera: Boolean) {
        var originalBm: Bitmap? = null
        var resizedBitmap: Bitmap? = null

        try {
            val options = BitmapFactory.Options()
            originalBm = BitmapFactory.decodeFile(file.absolutePath, options)

            if (isCamera) {
                try {
                    val exif = ExifInterface(file.absolutePath)
                    val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val exifDegree = exifOrientationToDegrees(exifOrientation)

                    originalBm = rotate(originalBm, exifDegree)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            val width = originalBm!!.width
            val height = originalBm.height

            val aspect: Float
            val scaleWidth: Float
            val scaleHeight: Float
            if (width > height) {
                if (width <= newWidth) return
                aspect = width.toFloat() / height
                scaleWidth = newWidth.toFloat()
                scaleHeight = scaleWidth / aspect
            } else {
                if (height <= newWidth) return
                aspect = height.toFloat() / width
                scaleHeight = newWidth.toFloat()
                scaleWidth = scaleHeight / aspect
            }

            val matrix = Matrix()

            // resize the bitmap
            matrix.postScale(scaleWidth / width, scaleHeight / height)

            // recreate the new Bitmap
            resizedBitmap = Bitmap.createBitmap(originalBm, 0, 0, width, height, matrix, true)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                resizedBitmap!!.compress(CompressFormat.JPEG, 80, FileOutputStream(newFile))
            } else {
                resizedBitmap!!.compress(CompressFormat.PNG, 80, FileOutputStream(newFile))
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            originalBm?.recycle()
            resizedBitmap?.recycle()
        }
    }

     //EXIF 정보를 회전각도로 변환
    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
         return when (exifOrientation) {
             ExifInterface.ORIENTATION_ROTATE_90 -> 90
             ExifInterface.ORIENTATION_ROTATE_180 -> 180
             ExifInterface.ORIENTATION_ROTATE_270 -> 270
             else -> 0
         }
    }

     //이미지 회전
    private fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? {
        var bitmap = bitmap
        if (degrees != 0 && bitmap != null) {
            val m = Matrix()
            m.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
            try {
                val converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
                if (bitmap != converted) {
                    bitmap.recycle()
                    bitmap = converted
                }
            } catch (ex: OutOfMemoryError) {
            }

        }
        return bitmap
    }
}
