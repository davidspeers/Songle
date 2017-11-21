package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_mode.*
import kotlinx.android.synthetic.main.activity_settings.*

class ModeActivity : AppCompatActivity() {

    private fun switchToMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private var buttonId = 1 // id of radio button selected
    val PREFS_FILE = "MyPrefsFile" // for storing preferences

    override fun onCreate(savedInstanceState: Bundle?) {
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

        when (buttonId) {
            0 -> easyRadioButton.setChecked(true)
            1 -> normalRadioButton.setChecked(true)
            2 -> hardRadioButton.setChecked(true)
            3 -> vHardRadioButton.setChecked(true)
            4 -> extremeRadioButton.setChecked(true)
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
