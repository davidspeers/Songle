package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_mode.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.util.*

class ModeActivity : AppCompatActivity() {

    private fun switchToMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    // Initialise NetworkReceiver class
    private var receiver = NetworkReceiver()

    // Initialise sharedpreferences
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var buttonId = 4 // id of radio button selected (normal default)
    private var colourId = 0
    private var currentSongNumber = 1
    private var currentSongName = ""
    private var currentSongArtist = ""
    private var currentSongLink = ""
    private var gameStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //Set correct theme colour
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme)
            1 -> setTheme(R.style.BlueTheme)
            2 -> setTheme(R.style.GreenTheme)
            3 -> setTheme(R.style.PurpleTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)

        // Register BroadcastReceiver to track connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(receiver, filter)

        //If connected to internet store sharedpreferences required for MapsActivity and go to MapsAcitivty
        playGameButton.setOnClickListener {
            if (receiver.connectedToInternet) {
                val editor = settings.edit()
                gameStarted = true
                editor.putBoolean("gameStarted", gameStarted)
                editor.putInt("LyricPointsEarned", 0)
                editor.apply()
                Log.v("gaming", gameStarted.toString())
                switchToMap()
            } else {
                longToast("You Need to be Connected to the Internet to Play Songle!")
            }
        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        //Choose Difficulty
        radioMode.setOnCheckedChangeListener({ _, optionId ->
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

        //If songsList hasn't been downloaded yet download here and then randomly pick a song
        if (songsList.size == 0) {
            doAsync {
                DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                uiThread {
                    val rand = Random()
                    val randomSong = songsList[rand.nextInt(songsList.size)]
                    Log.d("SongTitle", randomSong.title)
                    currentSongNumber = randomSong.number.toInt()
                    currentSongName = randomSong.title
                    currentSongArtist = randomSong.artist
                    currentSongLink = randomSong.link
                }
            }
        } else { //If songsList is already downloaded, just pick a song
            val rand = Random()
            val randomSong = songsList[rand.nextInt(songsList.size)]
            Log.d("SongTitle", randomSong.title)
            currentSongNumber = randomSong.number.toInt()
            currentSongName = randomSong.title
            currentSongArtist = randomSong.artist
            currentSongLink = randomSong.link
        }

    }


    override fun onStart() {
        super.onStart()
        // Restore preferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        buttonId = settings.getInt("storedModeId", 4) // 4 is normal mode and our default
        colourId = settings.getInt("storedColourId", 0)

        //Set the difficulty to the difficulty that was chosen last game
        when (buttonId) {
            5 -> easyRadioButton.setChecked(true)
            4 -> normalRadioButton.setChecked(true)
            3 -> hardRadioButton.setChecked(true)
            2 -> vHardRadioButton.setChecked(true)
            1 -> extremeRadioButton.setChecked(true)
        }

        //Obtain the colour of the play game button
        when (colourId) {
            0 -> playGameButton.setBackgroundResource(R.drawable.redstart)
            1 -> playGameButton.setBackgroundResource(R.drawable.bluestart)
            2 -> playGameButton.setBackgroundResource(R.drawable.greenstart)
            3 -> playGameButton.setBackgroundResource(R.drawable.purplestart)
        }
    }

    //OnPause save the following sharedpreferences
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
