package com.example.david.songlek

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val background = object : Thread() {
            override fun run() {
                Thread.sleep((500).toLong())

                startActivity(Intent(baseContext, MainActivity::class.java))
                finish()
            }

        }
        background.start()
    }
}
