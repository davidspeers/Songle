package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_win.*
import kotlinx.android.synthetic.main.content_main.*

class WinActivity : AppCompatActivity() {

    private var colourId = 0
    private var score = 0
    private var highscore = 0
    val PREFS_FILE = "MyPrefsFile" // for storing preferences

    private fun switchToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun switchToMode() {
        val intent = Intent(this, ModeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        score = settings.getInt("score", 0)
        highscore = settings.getInt("highscore", 0)
        val scoreText = "Score: " + score.toString()
        scoreView.text = scoreText
        if (highscore < score) {
            highscore = score
        }
        val highscoreText = "High Score: " + highscore
        highscoreView.text = highscoreText
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }

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

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("highscore", highscore)
        // Apply the edits!
        editor.apply()
    }

    override fun onBackPressed() {
        switchToMain()
    }


}
