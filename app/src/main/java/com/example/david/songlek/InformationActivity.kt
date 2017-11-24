package com.example.david.songlek

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class InformationActivity : AppCompatActivity() {

    private var colourId = 0
    val PREFS_FILE = "MyPrefsFile" // for storing preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}
