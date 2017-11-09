package com.example.david.songlek

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class InformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}
