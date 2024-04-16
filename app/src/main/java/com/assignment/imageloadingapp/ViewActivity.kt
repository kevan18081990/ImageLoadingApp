package com.assignment.imageloadingapp

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.assignment.caching.CustomCaching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewActivity : AppCompatActivity() {

    private lateinit var imageLoader: CustomCaching
    val URL1 = "https://i.pinimg.com/originals/93/09/77/930977991c52b48e664c059990dea125.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
    }

    fun initViews() {
        val imageView = findViewById<ImageView>(R.id.imageView)
        var imageBitmap: Bitmap? = null
        CoroutineScope(Dispatchers.Default).launch {
            imageBitmap = CustomCachingAsync(applicationContext).getBitmap(URL1)
            withContext(Dispatchers.Main){
                imageView.setImageBitmap(imageBitmap)
            }
        }
    }
}