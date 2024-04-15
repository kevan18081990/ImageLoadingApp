package com.assignment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object ImageUtil {
    suspend fun getBitmapFromUrl(imageUrl: String): ImageBitmap? {
        val bitmap = withContext(Dispatchers.IO) {
            mLoad(imageUrl)
        }
        return if(bitmap!=null){
            val imageBitmap = withContext(Dispatchers.Default) {
                val b = Bitmap.createScaledBitmap(bitmap, 8, 8, false)
                b.asImageBitmap()
            }
            imageBitmap
        }else{
            null
        }
    }

    private fun mLoad(strURL: String): Bitmap? {
        val url: URL? = mStringToURL(strURL)
        val connection: HttpURLConnection?
        try {
            connection = url?.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun getResizedBitmap(bm: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        if (bm == null) {
            return null
        }
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }
}
