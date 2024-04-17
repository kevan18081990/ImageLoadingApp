package com.assignment.caching

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.util.concurrent.Executors
import java.util.concurrent.Future
import com.assignment.caching.DiskCache

class CustomCaching constructor(context: Context, cacheSize: Int) {
    private val cache = DiskCache(context)
    private val executorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val mRunningDownloadList: HashMap<String, Future<Bitmap?>> = hashMapOf()

    fun displayImageNew(url: String, setPlaceHolder: () -> Unit, onLoad: (bitmap: Bitmap) -> Unit) {
        val bitmap = cache.get(url)
        bitmap?.let {
            onLoad(it)
            return
        } ?: run {
            setPlaceHolder()
            addDownloadImageTask(url, DownloadImageTask(url, cache) {
                onLoad(it)
            })
        }
    }

    fun addDownloadImageTask(url: String, downloadTask: DownloadTask<Bitmap?>) {
        mRunningDownloadList.put(url, executorService.submit(downloadTask))
    }

    fun clearcache() {
        cache.clear()
    }

    fun cancelTask(url: String) {
        synchronized(this) {
            mRunningDownloadList.forEach {
                if (it.key == url && !it.value.isDone)
                    it.value.cancel(true)
            }
        }
    }

    fun cancelAll() {
        synchronized(this) {
            mRunningDownloadList.forEach {
                if (!it.value.isDone)
                    it.value.cancel(true)
            }
            mRunningDownloadList.clear()
        }
    }

    companion object {
        private val INSTANCE: CustomCaching? = null

        @Synchronized
        fun getInstance(context: Context, cacheSize: Int = Config.defaultCacheSize): CustomCaching {
            return INSTANCE?.let { return INSTANCE }
                ?: run {
                    return CustomCaching(context, cacheSize)
                }
        }
    }
}

