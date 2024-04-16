package com.assignment.exp

import android.content.Context

import java.io.File

class FileCache(context: Context) {

    private var cacheDir: File? = null

    init {

        if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED)
            cacheDir = File(android.os.Environment.getExternalStorageDirectory(), "dir")
        else
            cacheDir = context.cacheDir
        if (!cacheDir!!.exists())
            cacheDir!!.mkdirs()
    }

    fun getFile(url: String): File {
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)

    }

    fun clear() {
        val files = cacheDir!!.listFiles() ?: return
        for (f in files)
            f.delete()
    }
}