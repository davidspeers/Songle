package com.example.david.songlek

import android.content.Context
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import org.jetbrains.anko.*;
import java.util.*

class ProfileActivity : AppCompatActivity() {

    val profile_list = ArrayList<String>()

    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var lyricPoints = 100
    private var highscore = 0
    private var unlockedSongNumbers = ""
    private var totalDistanceTravelled = 0
    private var totalTimePlayed : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }
        lyricPoints = settings.getInt("lyricPoints", 0)
        highscore = settings.getInt("highscore", 0)
        unlockedSongNumbers = settings.getString("unlockedSongNumbers", "")
        totalDistanceTravelled = settings.getInt("totalDistanceTravelled", 0)
        totalTimePlayed = settings.getLong("totalTimePlayed", 0)

        val unlockedSongsList = unlockedSongNumbers.split(",")
        val unlockedSongs = unlockedSongsList.size

        profile_list.add("Current Lyric Points: "+ lyricPoints)
        profile_list.add("High Score: "+ highscore)
        profile_list.add("Unlocked Achievements:")

        if (unlockedSongs > 0) profile_list.add("Unlocked 1 Songs")
        if (unlockedSongs > 4) profile_list.add("Unlocked 5 Songs")
        if (unlockedSongs > 9) profile_list.add("Unlocked 10 Songs")
        if (unlockedSongs > 14) profile_list.add("Unlocked 15 Songs")

        profile_list.add("Locked Achievements:")
        if (unlockedSongs < 1) profile_list.add("Unlocked 1 Songs")
        if (unlockedSongs < 5) profile_list.add("Unlocked 5 Songs")
        if (unlockedSongs < 10) profile_list.add("Unlocked 10 Songs")
        if (unlockedSongs < 15) profile_list.add("Unlocked 15 Songs")

        /*Log.v("ProfCheck", lyricPoints.toString())
        Log.v("ProfCheck", highscore.toString())
        Log.v("ProfCheck", unlockedSongs.toString())
        Log.v("ProfCheck", totalDistanceTravelled.toString())
        Log.v("ProfCheck", totalTimePlayed.toString())

        profile_list.add(lyricPoints.toString())
        profile_list.add(highscore.toString())
        profile_list.add(unlockedSongs.toString())
        profile_list.add(totalDistanceTravelled.toString())
        profile_list.add(totalTimePlayed.toString())*/

        var adapter=ProfileAdapter(profile_list)      //define adapter
        var ui = ProfileUI(adapter)                //define Anko UI Layout to be used
        ui.setContentView(this)                 //Set Anko UI to this Activity

    }
}
