package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_win.*

class WinActivity : AppCompatActivity() {

    //Instantiate sharedpreferences
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var score = 0
    private var highscore = 0
    private var lyricPointsEarned = 0

    private fun switchToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun switchToMode() {
        val intent = Intent(this, ModeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        //Set correct theme colour
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> {
                setTheme(R.style.RedTheme)
            }
            1 -> {
                setTheme(R.style.BlueTheme)
            }
            2 -> {
                setTheme(R.style.GreenTheme)
            }
            3 -> {
                setTheme(R.style.PurpleTheme)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)

        //Get sharedpreferences values from MapsActivity
        score = settings.getInt("score", 0)
        highscore = settings.getInt("highscore", 0)
        lyricPointsEarned = settings.getInt("lyricPointsEarned", 0)
        //Display Text
        val lyricPointsText = "You Gained A Total Of " + lyricPointsEarned + " Lyrics Points This Game."
        lyricPointsView.text = lyricPointsText
        val scoreText = "Score: " + score.toString()
        scoreView.text = scoreText
        if (highscore < score) {
            highscore = score
        }
        val highscoreText = "High Score: " + highscore
        highscoreView.text = highscoreText

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        mainMenuButton.setOnClickListener() {
            switchToMain()
        }

        playAgainButton.setOnClickListener() {
            switchToMode()
        }

    }

    override fun onStop() {
        super.onStop()
        //Save sharedpreferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("highscore", highscore)
        // Apply the edits!
        editor.apply()
    }

    //Stop user from going back to MapsActivity
    override fun onBackPressed() {
        switchToMain()
    }


}
