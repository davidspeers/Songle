package com.example.david.songlek

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mode.*

class ModeActivity : AppCompatActivity() {

    private fun switchToMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)

        easyButton.setOnClickListener() {

            switchToMap()
        }
    }


}
