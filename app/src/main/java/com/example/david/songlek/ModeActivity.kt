package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_mode.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

val songsList = ArrayList<XMLSongParser.Song>()

class ModeActivity : AppCompatActivity() {

    private fun switchToMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private var buttonId = 1 // id of radio button selected
    private var colourId = 0
    private var currentSongNumber = 1
    private var currentSongName = ""
    private var currentSongArtist = ""
    private var currentSongLink = ""
    private var gameStarted = false
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
            val editor = settings.edit()
            gameStarted = true
            editor.putBoolean("gameStarted", gameStarted)
            editor.putInt("LyricPointsEarned", 0)
            editor.apply()
            Log.v("gaming", gameStarted.toString())
            switchToMap()
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        radioMode.setOnCheckedChangeListener({ radioGroup, optionId ->
            when (optionId) {
                R.id.easyRadioButton -> {
                    modeDescription.setText(R.string.easyModeParagraph)
                    buttonId = 5
                }
                R.id.normalRadioButton -> {
                    modeDescription.setText(R.string.normalModeParagraph)
                    buttonId = 4
                }
                R.id.hardRadioButton -> {
                    modeDescription.setText(R.string.hardModeParagraph)
                    buttonId = 3
                }
                R.id.vHardRadioButton -> {
                    modeDescription.setText(R.string.vHardModeParagraph)
                    buttonId = 2
                }
                R.id.extremeRadioButton -> {
                    modeDescription.setText(R.string.extremeModeParagraph)
                    buttonId = 1
                }
            }
        })

        doAsync {
            DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
            uiThread {
                val rand = Random()
                val randomSong = songsList.get(rand.nextInt(songsList.size))
                Log.v("random", randomSong.title)
                currentSongNumber = randomSong.number.toInt()
                currentSongName = randomSong.title
                currentSongArtist = randomSong.artist
                currentSongLink = randomSong.link
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Restore preferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        buttonId = settings.getInt("storedModeId", 1)
        colourId = settings.getInt("storedColourId", 0)

        when (buttonId) {
            5 -> easyRadioButton.setChecked(true)
            4 -> normalRadioButton.setChecked(true)
            3 -> hardRadioButton.setChecked(true)
            2 -> vHardRadioButton.setChecked(true)
            1 -> extremeRadioButton.setChecked(true)
        }

        when (colourId) {
            0 -> playGameButton.setBackgroundResource(R.drawable.redstart)
            1 -> playGameButton.setBackgroundResource(R.drawable.bluestart)
            2 -> playGameButton.setBackgroundResource(R.drawable.greenstart)
            3 -> playGameButton.setBackgroundResource(R.drawable.purplestart)
        }
    }

    override fun onPause() {
        super.onPause()
        // All objects are from android.context.Context
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("storedModeId", buttonId)
        editor.putInt("currentSongNumber", currentSongNumber)
        editor.putString("currentSongName", currentSongName)
        editor.putString("currentSongArtist", currentSongArtist)
        editor.putString("currentSongLink", currentSongLink)
        // Apply the edits!
        editor.apply()
    }


}
