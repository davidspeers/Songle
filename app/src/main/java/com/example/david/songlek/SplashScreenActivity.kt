package com.example.david.songlek

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //let the splashscreen run for 1 second
        val background = object : Thread() {
            override fun run() {
                Thread.sleep((1000).toLong())

                startActivity(Intent(baseContext, MainActivity::class.java))
                finish() //now backbutton pressed from MainActivity doesn't go back to SplashScreenActivity
            }

        }
        background.start()
    }
}
