package com.assignment.imageloadingapp

import android.content.Context
import android.graphics.Bitmap
import com.assignment.caching.CustomCaching
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CustomCachingAsync(context: Context) {

    private var imageLoader: CustomCaching

    init {
        imageLoader = CustomCaching.getInstance(context, 4194304) //4MiB
    }

    suspend fun getBitmap(url: String): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            imageLoader.displayImageNew(url, setPlaceHolder = {
                if(continuation.isActive) continuation.resume(null)
            }) {
                if(continuation.isActive) continuation.resume(it)
            }
        }
    }

}