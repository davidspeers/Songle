package com.example.david.songlek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Xml
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_main.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var colourId = 0 // id of radio button selected
    private var gameStarted = false //whether or not a game has been started
    val PREFS_FILE = "MyPrefsFile" // for storing preferences

    private var receiver = NetworkReceiver()
    private inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                //Snackbar saying it doesn't work
            }
        }
    }

    private fun switchToGame() {
        if (!gameStarted) {
            val intent = Intent(this, ModeActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun switchToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun switchToInformation() {
        val intent = Intent(this, InformationActivity::class.java)
        startActivity(intent)
    }

    private fun switchToUnlockedSongs() {
        val intent = Intent(this, UnlockedSongsActivity::class.java)
        startActivity(intent)
    }

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
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        gameStarted = settings.getBoolean("gameStarted", false)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register BroadcastReceiver to track connection changes.
        var filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(receiver, filter)

        playSongleButton.setOnClickListener() {
            switchToGame()
        }

        profileButton.setOnClickListener() {
            switchToProfile()
        }

        informationButton.setOnClickListener() {
            switchToInformation()
        }

        unlockedSongsButton.setOnClickListener() {
            switchToUnlockedSongs()
        }

        val caller = DownloadCompleteListener()

        DownloadXmlTask(caller).execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
// Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

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

val songsList = ArrayList<XMLSongParser.Song>()

class DownloadCompleteListener {
    fun downloadComplete(result: String){
        //create snackbar result, if result not empty print success
        Log.v("Outside", result)
        //for (song in result) {
        //    Log.v("Outside", song)
        //}
        for (song in songsList) {
            Log.v("OutOfLoop", song.title)
        }


    }

}

class DownloadXmlTask(private val caller : DownloadCompleteListener) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg urls: String): String {
        return try {
            loadXmlFromNetwork(urls[0])
        } catch (e: IOException) {
            "Unable to load content. Check your network connection"
        } catch (e: XmlPullParserException) {
            "Error parsing XML"
        }
    }

    private fun loadXmlFromNetwork(urlString: String): String {
        val result = StringBuilder()
        val stream = downloadUrl(urlString)
        val parsedSongs = XMLSongParser().parse(stream)
        result.append(parsedSongs.toString())
        for (song in parsedSongs) {
            songsList.add(song)
        }
        return result.toString()
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        // Also available: HttpsURLConnection
        conn.readTimeout = 10000 // milliseconds
        conn.connectTimeout = 15000 // milliseconds
        conn.requestMethod = "GET"
        conn.doInput = true
        // Starts the query
        conn.connect()
        return conn.inputStream
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        caller.downloadComplete(result)
    }
}

