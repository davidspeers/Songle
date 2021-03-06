package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.*

class SettingsActivity : AppCompatActivity() {

    //Initialise sharedprefernces
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var highscore = 0
    private var allAchievementsUnlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        //Set correct theme colour
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme)
            1 -> setTheme(R.style.BlueTheme)
            2 -> setTheme(R.style.GreenTheme)
            3 -> setTheme(R.style.PurpleTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        //Clicked radiobutton is current the theme of the app
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

        //Is green button unlocked
        highscore = settings.getInt("highscore", 0)
        var greenClickable = false
        if (highscore>1000) greenClickable = true
        if (greenClickable) {
            greenButton.setEnabled(true)
        } else {
            greenButton.setEnabled(false)
        }
        //Is purple button unlocked
        allAchievementsUnlocked = settings.getBoolean("allAchievementsUnlocked", false)
        if (allAchievementsUnlocked) {
            purpleButton.setEnabled(true)
        } else {
            purpleButton.setEnabled(false)
        }

        //Reset all sharedpreferences and settings in order to reset all progress
        resetProgressButton.setOnClickListener() {
            alert("Are You Sure You Want To Delete All Your Progress?") {
                positiveButton("I'm Sure") {
                    settings.edit().clear().apply()
                    //Log.v("checker", settings.getInt("storedColourId", 0).toString())
                    //settings.edit().putInt("storedColourId", 0).apply()
                    colourId = 0 //change colourId to default
                    switchToMain()
                    toast("Progress Deleted") }
                negativeButton("Cancel") { }
            }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Restore preferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        colourId = settings.getInt("storedColourId", 0)

        //set the radio button group to the current theme colour
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

    private fun switchToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //This is a workaround so that the setTheme function in MainActivity gets called
    override fun onBackPressed() {
        switchToMain()
    }
}
