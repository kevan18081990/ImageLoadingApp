package com.assignment.caching

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import java.net.HttpURLConnection
import java.net.URL

class DownloadImageTaskNew(
    private val url: String,
    private val cache: DiskCache,
) : DownloadTask<Bitmap?>() {

    override fun download(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val url = URL(url)
            val conn: HttpURLConnection = url.openConnection() as
                    HttpURLConnection
            bitmap = BitmapFactory.decodeStream(conn.inputStream)
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun call(): Bitmap? {
        val bitmap = download(url)
        bitmap?.let {
            cache.put(url, it)
        }
        return bitmap
    }
}