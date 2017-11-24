package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_mode.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.*

class SettingsActivity : AppCompatActivity() {

    private var colourId = 0 // id of radio button selected
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
        setContentView(R.layout.activity_settings)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        radioColour.setOnCheckedChangeListener({ radioGroup, optionId ->
            when (optionId) {
                R.id.redButton -> {
                    colourId = 0
                }
                R.id.blueButton -> {
                    colourId = 1
                }
                R.id.greenButton -> {
                    colourId = 2
                }
                R.id.purpleButton -> {
                    colourId = 3
                }
            }
        })

        var greenClickable = true
        if (greenClickable) {
            greenButton.setEnabled(true)
        } else {
            greenButton.setEnabled(false)
        }
        var purpleClickable = true
        if (purpleClickable) {
            purpleButton.setEnabled(true)
        } else {
            purpleButton.setEnabled(false)
        }

        resetProgressButton.setOnClickListener() {
            alert("Are You Sure You Want To Delete All Your Progress?") {
                yesButton { toast("Yess!!!") }
                noButton { }
            }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Restore preferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        colourId = settings.getInt("storedColourId", 0)

        when (colourId) {
            0 -> redButton.setChecked(true)
            1 -> blueButton.setChecked(true)
            2 -> greenButton.setChecked(true)
            3 -> purpleButton.setChecked(true)
        }
    }

    override fun onPause() {
        super.onPause()
        // All objects are from android.context.Context
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("storedColourId", colourId)
        // Apply the edits!
        editor.apply()
    }
}
