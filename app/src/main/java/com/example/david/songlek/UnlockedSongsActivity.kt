package com.example.david.songlek

import android.content.Context
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import org.jetbrains.anko.*;
import java.util.*

class UnlockedSongsActivity : AppCompatActivity() {

    //List of all the songs we've unlocked
    val song_list = ArrayList<XMLSongParser.Song>()

    //initialise sharedpreferences
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var unlockedSongNumbers = ""
    private var colourId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Set correct theme colour
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }

        //Populating song_list to be displayed in ListView
        unlockedSongNumbers = settings.getString("unlockedSongNumbers", "")
        Log.d("displayUnlockedSongs", unlockedSongNumbers)
        val unlockedSongsList = unlockedSongNumbers.split(",")
        for (songNumber in unlockedSongsList) {
            if (songNumber == "") {
                //Do Nothing
            } else {
                if (songNumber.toInt() == -1) {
                    //do nothing (it's a duplicate)
                } else {
                    if (songNumber.toInt() >= songsList.size) {
                        //Do nothing (avoids possible null pointer exception)
                    } else {
                        song_list.add(songsList[songNumber.toInt()-1]) // -1 because of zero-indexing
                        Log.d("displayUnlockedSongs", songsList[songNumber.toInt()-1].title)
                    }
                }
            }
        }

        val adapter=UnlockedSongsAdapter(song_list)      //define adapter
        val ui = UnlockedSongsUI(adapter)                //define Anko UI Layout to be used
        ui.setContentView(this)                 //Set Anko UI to this Activity

    }

}