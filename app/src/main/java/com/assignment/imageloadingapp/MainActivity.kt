package com.assignment.imageloadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.imageloadingapp.data.ApiClient
import com.assignment.imageloadingapp.data.ApiService
import com.assignment.imageloadingapp.ui.theme.ImageLoadingAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageLoadingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    getURL()
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageLoadingAppTheme {
        Greeting("Android")
    }
}

fun getURL(){
    val call = ApiClient.apiService.getPhotos(10)

    call.enqueue(object : Callback<Any> {
        override fun onResponse(call: Call<Any>, response: Response<Any>) {
            if (response.isSuccessful) {
                val post = response.body()
                // Handle the retrieved post data
                println(post)
            } else {
                println(response.message())
            }
        }

        override fun onFailure(call: Call<Any>, t: Throwable) {
            // Handle failure
            println(t.message)
        }
    })
}