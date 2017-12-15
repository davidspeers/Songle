package com.example.david.songlek

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.jetbrains.anko.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    //All the strings we want to list in the ListView
    private val profile_list = ArrayList<String>()

    //initialise sharedpreferences
    private val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var lyricPoints = 100
    private var totalLyricPoints = 100
    private var highscore = 0
    private var unlockedSongNumbers = ""
    private var totalDistanceTravelled = 0
    private var totalTimePlayed : Long = 0
    private var changedTheme = false
    private var allAchievementsUnlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme)
            1 -> setTheme(R.style.BlueTheme)
            2 -> setTheme(R.style.GreenTheme)
            3 -> setTheme(R.style.PurpleTheme)
        }
        lyricPoints = settings.getInt("lyricPoints", 0)
        totalLyricPoints = settings.getInt("totalLyricPoints", 0)
        highscore = settings.getInt("highscore", 0)
        unlockedSongNumbers = settings.getString("unlockedSongNumbers", "")
        totalDistanceTravelled = settings.getInt("totalDistanceTravelled", 0)
        totalTimePlayed = settings.getLong("totalTimePlayed", 0)
        changedTheme = settings.getBoolean("changedTheme", false)

        val unlockedSongsList = unlockedSongNumbers.split(",")
        val unlockedSongs = unlockedSongsList.size

        profile_list.add("Current Lyric Points: "+ lyricPoints)
        profile_list.add("High Score: "+ highscore)

        //List Unlocked Achievements
        profile_list.add("Unlocked Achievements:")
        if (unlockedSongs > 0) profile_list.add("Unlocked 1 Songs")
        if (unlockedSongs > 4) profile_list.add("Unlocked 5 Songs")
        if (unlockedSongs > 9) profile_list.add("Unlocked 10 Songs")
        if (unlockedSongs > 14) profile_list.add("Unlocked 15 Songs")

        if (highscore >= 250) profile_list.add("Get a High Score greater than 250")
        if (highscore >= 500) profile_list.add("Get a High Score greater than 500")
        if (highscore >= 1000) profile_list.add("Get a High Score greater than 1000")
        if (highscore >= 1500) profile_list.add("Get a High Score greater than 1500")

        if (totalLyricPoints > 100) profile_list.add("Collect More than 100 Lyric Points")
        if (totalLyricPoints > 250) profile_list.add("Collect More than 250 Lyric Points")
        if (totalLyricPoints > 500) profile_list.add("Collect More than 500 Lyric Points")
        if (totalLyricPoints > 750) profile_list.add("Collect More than 750 Lyric Points")

        if (totalDistanceTravelled >= 1000) profile_list.add("Walk For 1 Kilometer")
        if (totalDistanceTravelled >= 5000) profile_list.add("Walk For 5 Kilometers")
        if (totalDistanceTravelled >= 10000) profile_list.add("Walk For 10 Kilometers")
        if (totalDistanceTravelled >= 20000) profile_list.add("Walk For 20 Kilometers")

        //convert from milliseconds to hours
        if (totalTimePlayed/3600000 >= 1) profile_list.add("Play For 1 Hours")
        if (totalTimePlayed/3600000 >= 2) profile_list.add("Play For 2 Hours")
        if (totalTimePlayed/3600000 >= 5) profile_list.add("Play For 5 Hours")
        if (totalTimePlayed/3600000 >= 10) profile_list.add("Play For 10 Hours")

        if (changedTheme) profile_list.add("Changed the Theme")

        //List Locked Achievements
        profile_list.add("Locked Achievements:")
        if (unlockedSongs < 1) profile_list.add("Unlocked 1 Songs")
        if (unlockedSongs < 5) profile_list.add("Unlocked 5 Songs")
        if (unlockedSongs < 10) profile_list.add("Unlocked 10 Songs")
        if (unlockedSongs < 15) profile_list.add("Unlocked 15 Songs")

        if (highscore < 250) profile_list.add("Get a High Score greater than 250")
        if (highscore < 500) profile_list.add("Get a High Score greater than 500")
        if (highscore < 1000) profile_list.add("Get a High Score greater than 1000")
        if (highscore < 1500) profile_list.add("Get a High Score greater than 1500")

        if (totalLyricPoints <= 100) profile_list.add("Collect More than 100 Lyric Points")
        if (totalLyricPoints <= 250) profile_list.add("Collect More than 250 Lyric Points")
        if (totalLyricPoints <= 500) profile_list.add("Collect More than 500 Lyric Points")
        if (totalLyricPoints <= 750) profile_list.add("Collect More than 750 Lyric Points")

        if (totalDistanceTravelled < 1000) profile_list.add("Walk For 1 Kilometer")
        if (totalDistanceTravelled < 5000) profile_list.add("Walk For 5 Kilometers")
        if (totalDistanceTravelled < 10000) profile_list.add("Walk For 10 Kilometers")
        if (totalDistanceTravelled < 20000) profile_list.add("Walk For 20 Kilometers")

        //convert from milliseconds to hours
        if (totalTimePlayed/3600000 < 1) profile_list.add("Play For 1 Hours")
        if (totalTimePlayed/3600000 < 2) profile_list.add("Play For 2 Hours")
        if (totalTimePlayed/3600000 < 5) profile_list.add("Play For 5 Hours")
        if (totalTimePlayed/3600000 < 10) profile_list.add("Play For 10 Hours")

        if (!changedTheme) profile_list.add("Changed the Theme (Hint: Go to Settings)")

        //Check if all achievements are unlocked
        if (unlockedSongs>14 && highscore>=1500 && totalLyricPoints>750 && totalDistanceTravelled>=20000 && totalTimePlayed/3600000>=10 && changedTheme ) allAchievementsUnlocked = true

        //Check if the ListView is correct
        Log.d("Achievements", totalLyricPoints.toString())
        Log.d("Achievements", highscore.toString())
        Log.d("Achievements", unlockedSongs.toString())
        Log.d("Achievements", totalDistanceTravelled.toString())
        Log.d("Achievements", totalTimePlayed.toString())


        val adapter=ProfileAdapter(profile_list)      //define adapter
        val ui = ProfileUI(adapter)                //define Anko UI Layout to be used
        ui.setContentView(this)                 //Set Anko UI to this Activity

    }

    //Save allAchievementsUnlocked Boolean required to unlock the purple theme in SettingsActivity
    override fun onPause() {
        super.onPause()
        // All objects are from android.context.Context
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putBoolean("allAchievementsUnlocked", allAchievementsUnlocked)
        // Apply the edits!
        editor.apply()
    }
}
