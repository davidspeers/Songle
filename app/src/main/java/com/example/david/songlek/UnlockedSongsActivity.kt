package com.example.david.songlek

/*import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class UnlockedSongsActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_unlocked_songs)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}*/
import android.content.Context
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import org.jetbrains.anko.*;
import java.util.*

class UnlockedSongsActivity : AppCompatActivity() {

    val task_list = ArrayList<XMLSongParser.Song>()         //list consisting of tasks

    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var unlockedSongNumbers = ""
    private var colourId = 0

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

        unlockedSongNumbers = settings.getString("unlockedSongNumbers", "")
        Log.v("unlockedSongs", unlockedSongNumbers)
        val unlockedSongsList = unlockedSongNumbers.split(",")
        for (songNumber in unlockedSongsList) {
            //add if songslist < greatest number
            if (songNumber == "") {
                //Do Nothing
            } else {
                //Need a check that songslist is > 1 (else you get an error if it hasn't loaded)
                task_list.add(songsList[songNumber.toInt()-1])
                Log.v("unlockedSongs", songsList[songNumber.toInt()-1].title)
            }
        }




        /*savedInstanceState?.let {
            val arrayList = savedInstanceState.get("ToDoList")
            task_list.addAll(arrayList as List<String>)
        }*/
        var adapter=UnlockedSongsAdapter(task_list)      //define adapter
        var ui = UnlockedSongsUI(adapter)                //define Anko UI Layout to be used
        ui.setContentView(this)                 //Set Anko UI to this Activity

    }
    /*override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putStringArrayList("ToDoList", task_list)
        super.onSaveInstanceState(outState)
    }*/
}