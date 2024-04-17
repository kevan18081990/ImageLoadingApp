package com.assignment.imageloadingapp

import android.content.Context
import com.assignment.caching.ImageLoader

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