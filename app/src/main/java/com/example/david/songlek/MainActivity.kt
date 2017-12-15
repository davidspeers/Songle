package com.example.david.songlek

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast

class MainActivity : AppCompatActivity() {

    //Initialise sharedpreferences
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var newGame = false //whether or not a new game needs to start

    // Initialise NetworkReceiver class
    private var receiver = NetworkReceiver()

    //Either go to ModeActivity/MapsActivity depending on whether or not a newGame needs to start
    private fun switchToGame() {
        if (newGame) {
            val intent = Intent(this, ModeActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    //Go to ProfileActivity
    private fun switchToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    //Go to InformationActivity
    private fun switchToInformation() {
        val intent = Intent(this, InformationActivity::class.java)
        startActivity(intent)
    }

    //Go to unlockedSongsActivity
    private fun switchToUnlockedSongs() {
        val intent = Intent(this, UnlockedSongsActivity::class.java)
        startActivity(intent)
    }

    //When resuming MainActivity make sure buttons and theme colour are correct
    override fun onResume() {
        super.onResume()
        when (colourId) {
            0 -> {
                playSongleButton.setBackgroundResource(R.drawable.redplay)
                unlockedSongsButton.setBackgroundResource(R.drawable.redunlock)
                profileButton.setBackgroundResource(R.drawable.redprofile)
                informationButton.setBackgroundResource(R.drawable.redinfo)
                setTheme(R.style.RedTheme)
            }
            1 -> {
                playSongleButton.setBackgroundResource(R.drawable.blueplay)
                unlockedSongsButton.setBackgroundResource(R.drawable.blueunlock)
                profileButton.setBackgroundResource(R.drawable.blueprofile)
                informationButton.setBackgroundResource(R.drawable.blueinfo)
                setTheme(R.style.BlueTheme)
            }
            2 -> {
                playSongleButton.setBackgroundResource(R.drawable.greenplay)
                unlockedSongsButton.setBackgroundResource(R.drawable.greenunlock)
                profileButton.setBackgroundResource(R.drawable.greenprofile)
                informationButton.setBackgroundResource(R.drawable.greeninfo)
                setTheme(R.style.GreenTheme)
            }
            3 -> {
                playSongleButton.setBackgroundResource(R.drawable.purpleplay)
                unlockedSongsButton.setBackgroundResource(R.drawable.purpleunlock)
                profileButton.setBackgroundResource(R.drawable.purpleprofile)
                informationButton.setBackgroundResource(R.drawable.purpleinfo)
                setTheme(R.style.PurpleTheme)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //get sharedpreferences values
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        newGame = settings.getBoolean("newGame", true)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme)
            1 -> setTheme(R.style.BlueTheme)
            2 -> setTheme(R.style.GreenTheme)
            3 -> setTheme(R.style.PurpleTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register BroadcastReceiver to track connection changes.
        // the receiver class also calls the DownloadXMltask for us when we get an internet connection
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(receiver, filter)

        playSongleButton.setOnClickListener() {
            if (receiver.connectedToInternet) {
                switchToGame()
            } else {
                longToast("You Need to be Connected to the Internet to Play Songle!")
            }
        }

        profileButton.setOnClickListener() {
            switchToProfile()
        }

        informationButton.setOnClickListener() {
            switchToInformation()
        }

        unlockedSongsButton.setOnClickListener() {
            if (receiver.connectedToInternet) {
                switchToUnlockedSongs()
            } else {
                longToast("You Need to be Connected to the Internet to Access Your Songs!")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //This function adds the click-wheel to the top right of our MainActivity that goes to the SettingsActivity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_setting ->
    // User chose the ”Settings” item, show the app settings UI
                {startActivity(Intent(this, SettingsActivity::class.java))
                return true}
            else ->
    // If we got here, the user’s action was not recognised.
    // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item)
        }
    }

}
