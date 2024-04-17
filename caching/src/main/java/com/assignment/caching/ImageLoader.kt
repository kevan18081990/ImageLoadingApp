package com.assignment.caching

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class ImageLoader(context: Context) {

    internal var memoryCache = MemoryCache()
    internal var fileCache: FileCache
    private val imageViewIdMap = Collections.synchronizedMap(WeakHashMap<String, String>())
    internal var executorService: ExecutorService
    internal var handler = Handler()

    init {
        fileCache = FileCache(context)
        executorService = Executors.newFixedThreadPool(5)
    }

    fun displayImage(url: String, imageViewId: String, onLoadBitmap: (bitmap: Bitmap) -> Unit) {
        imageViewIdMap[imageViewId] = url
        val bitmap = memoryCache[url]
        bitmap?.let {
            onLoadBitmap(it)
        } ?: kotlin.run {
            queuePhoto(url, imageViewId, onLoadBitmap)
        }
    }

    suspend fun getBitmap(url: String, imageViewId: String): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            displayImage(url, imageViewId, onLoadBitmap = {
                if (continuation.isActive) continuation.resume(it)
            })
        }
    }

    private fun queuePhoto(
        url: String,
        imageViewId: String,
        onLoadBitmap: (bitmap: Bitmap) -> Unit
    ) {
        val p = PhotoToLoad(url, imageViewId)
        executorService.submit(PhotosLoader(p,onLoadBitmap))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)

        val b = decodeFile(f)
        if (b != null)
            return b

        try {
            var bitmap: Bitmap? = null
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val `is` = conn.inputStream
            val os = FileOutputStream(f)
            copyStream(`is`, os)
            os.close()
            conn.disconnect()
            bitmap = decodeFile(f)
            return bitmap
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError)
                memoryCache.clear()
            return null
        }

    }

    private fun decodeFile(f: File): Bitmap? {
        try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()

            val REQUIRED_SIZE = 140
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private inner class PhotoToLoad(var url: String, var imageViewId: String)

    private inner class PhotosLoader(val photoToLoad: PhotoToLoad, val onLoadBitmap: (bitmap: Bitmap) -> Unit) : Runnable {

        override fun run() {
            try {
                if (imageViewReused(photoToLoad))
                    return
                val bmp = getBitmap(photoToLoad.url)
                memoryCache.put(photoToLoad.url, bmp!!)
                if (imageViewReused(photoToLoad))
                    return
                val bd = BitmapDisplayer(bmp, photoToLoad, onLoadBitmap)
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }

        }
    }

    private fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViewIdMap[photoToLoad.imageViewId]
        return if (tag == null || tag != photoToLoad.url) true else false
    }

    private inner class BitmapDisplayer(
        val bitmap: Bitmap?,
        val photoToLoad: PhotoToLoad,
        val onLoadBitmap: (bitmap: Bitmap) -> Unit
    ) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            if (bitmap != null)
                onLoadBitmap(bitmap)
            //else
            //TODO load placeholder
        }
    }

    fun clearCache() {
        memoryCache.clear()
        fileCache.clear()
    }

    companion object {

        fun copyStream(`is`: InputStream, os: OutputStream) {
            val buffer_size = 1024
            try {
                val bytes = ByteArray(buffer_size)
                while (true) {
                    val count = `is`.read(bytes, 0, buffer_size)
                    if (count == -1)
                        break
                    os.write(bytes, 0, count)
                }
            } catch (ex: Exception) {
            }

        }
    }
}