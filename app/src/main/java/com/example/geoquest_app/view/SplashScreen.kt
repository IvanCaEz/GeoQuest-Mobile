package com.example.geoquest_app.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.geoquest_app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Esconder ActionBar
        supportActionBar?.hide()

        //Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        }

    }

}
