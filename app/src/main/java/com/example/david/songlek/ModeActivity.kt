package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mode.*

class ModeActivity : AppCompatActivity() {

    private fun switchToMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private var buttonId = 1 // id of radio button selected
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
        setContentView(R.layout.activity_mode)

        playGameButton.setOnClickListener() {
            switchToMap()
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        radioMode.setOnCheckedChangeListener({ radioGroup, optionId ->
            when (optionId) {
                R.id.easyRadioButton -> {
                    modeDescription.setText(R.string.easyModeParagraph)
                    buttonId = 0
                }
                R.id.normalRadioButton -> {
                    modeDescription.setText(R.string.normalModeParagraph)
                    buttonId = 1
                }
                R.id.hardRadioButton -> {
                    modeDescription.setText(R.string.hardModeParagraph)
                    buttonId = 2
                }
                R.id.vHardRadioButton -> {
                    modeDescription.setText(R.string.vHardModeParagraph)
                    buttonId = 3
                }
                R.id.extremeRadioButton -> {
                    modeDescription.setText(R.string.extremeModeParagraph)
                    buttonId = 4
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Restore preferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        buttonId = settings.getInt("storedModeId", 1)
        colourId = settings.getInt("storedColourId", 0)

        when (buttonId) {
            0 -> easyRadioButton.setChecked(true)
            1 -> normalRadioButton.setChecked(true)
            2 -> hardRadioButton.setChecked(true)
            3 -> vHardRadioButton.setChecked(true)
            4 -> extremeRadioButton.setChecked(true)
        }

        when (colourId) {
            0 -> playGameButton.setBackgroundResource(R.drawable.redstart)
            1 -> playGameButton.setBackgroundResource(R.drawable.bluestart)
            2 -> playGameButton.setBackgroundResource(R.drawable.greenstart)
            3 -> playGameButton.setBackgroundResource(R.drawable.purplestart)
        }
    }

    override fun onStop() {
        super.onStop()
        // All objects are from android.context.Context
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("storedModeId", buttonId)
        // Apply the edits!
        editor.apply()
    }


}
