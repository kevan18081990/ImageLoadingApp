package com.assignment.imageloadingapp

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Room
import androidx.room.RoomDatabase
import com.assignment.caching.CustomCaching
import com.assignment.exp.ImageLoader
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CustomImageLoader {
    companion object {

        @Volatile
        private var INSTANCE: ImageLoader? = null

        fun getInstance(context: Context): ImageLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ImageLoader(context).also { INSTANCE = it }
            }

    }
}