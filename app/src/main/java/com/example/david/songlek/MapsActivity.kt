package com.example.david.songlek

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource
import com.google.android.gms.maps.model.Marker
import org.jetbrains.anko.*
import java.net.URL
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.design.coordinatorLayout
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.net.HttpURLConnection
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val permissionsRequestAccessFineLocation = 1
    var mLocationPermissionGranted = false
    private var mLastLocation: Location? = null
    val TAG = "MapsActivity"
    val markerHash = HashMap<String, Marker>()

    var collectedLyricsCount = 0
    val collectedLyrics = ArrayList<String>()
    val song = "Bohemian Rhapsody"
    var lyricPoints = 100
    val artistsName = "Queen"

    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var songNumber = 1
    private var difficulty = 1
    private var score = 0
    private var incorrectGuesses = 0

    private fun switchToMode() {
        val intent = Intent(this, ModeActivity::class.java)
        startActivity(intent)
    }

    private fun switchToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun switchToWin() {
        val intent = Intent(this, WinActivity::class.java)
        startActivity(intent)

        //Values to reset
        val currentGame = false


        //Calculate Final Score
        //Difficulty Modifier
        when (difficulty) {
            1 -> {
                score = 250
                score += markerHash.size*10 //Markers Left Bonus
            }
            2 -> {
                score = 200
                score += markerHash.size*5 //Markers Left Bonus
            }
            3 -> {
                score = 150
                score += markerHash.size*3 //Markers Left Bonus
            }
            4 -> {
                score = 100
                score += markerHash.size*2 //Markers Left Bonus
            }
            5 -> {
                score = 50
                score += markerHash.size*2 //Markers Left Bonus
            }
        }
        score -= incorrectGuesses*5 //Guess Penalty
        if (score<0) score = 0 //Avoid Negative Score

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("score", score)
        // Apply the edits!
        editor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        collectedMarkers = settings.getString("collectedMarkers", "")
        Log.v("collected", collectedMarkers)
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme);
            1 -> setTheme(R.style.BlueTheme);
            2 -> setTheme(R.style.GreenTheme);
            3 -> setTheme(R.style.PurpleTheme);
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // Get notified when the map is ready to be used. Long-running activities are performed asynchronously in order to keep the user interface responsive
        mapFragment.getMapAsync(this)

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
// Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onBackPressed() {
        switchToMain()
    }

    fun buyUnclassified() {
        if (lyricPoints > 24) {
            val marker = uncollectedMarkersList[0]
            uncollectedMarkersList.remove(marker)
            collectedMarkers = collectedMarkers + "," + marker.name
            val markerName = markerHash.get(marker.name)
            if (markerName == null) {
                //Do nothing
            } else {
                markerName.remove()
                val points = marker.name.split(":")
                val collectedLyric = lyrics.get(points[0].toInt() - 1)[points[1].toInt() - 1]
                collectedLyrics.add(collectedLyric)
                Log.v("lyrics", collectedLyric)
                markerHash.remove(marker.name)
                val snackbarBoughtWord = Snackbar.make(findViewById(R.id.map_Layout), "The Word You Bought Is: " + collectedLyric, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Open Lyrics List", View.OnClickListener() {
                            collectedLyricsCount = 0
                            Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                            alert(TextUtils.join(", ", collectedLyrics))
                            { title("Collected Lyrics") }.show()
                        })
                snackbarBoughtWord.setActionTextColor(Color.WHITE)
                snackbarBoughtWord.show()
                lyricPoints = lyricPoints - 25
            }
        } else {
            notEnoughPoints()
        }
    }

    fun buyClassified(string:String, wordCost:Int) {
        if (lyricPoints > wordCost-1) {
            var m = uncollectedMarkersList[0]

            loop@ for (marker in uncollectedMarkersList) {
                m = marker
                if (marker.description == string) break@loop
            }

            //Checks if m is not of the requested description
            if (m.description != string) {
                val snackbarBoughtWord = Snackbar.make(findViewById(R.id.map_Layout), "There Are No " + string + "Left to Buy", Snackbar.LENGTH_LONG)
                    .setAction("Open Lyrics List", View.OnClickListener() {
                        Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                        alert(TextUtils.join(", ", collectedLyrics))
                        { title("Collected Lyrics") }.show()
                    })
            snackbarBoughtWord.setActionTextColor(Color.WHITE)
            snackbarBoughtWord.show()
            } else {
                uncollectedMarkersList.remove(m)
                collectedMarkers = collectedMarkers + "," + m.name
                val markerName = markerHash.get(m.name)
                if (markerName == null) {
                    //Do nothing
                } else {
                    markerName.remove()
                    val points = m.name.split(":")
                    val collectedLyric = lyrics.get(points[0].toInt() - 1)[points[1].toInt() - 1]
                    collectedLyrics.add(collectedLyric)
                    Log.v("lyrics", collectedLyric)
                    markerHash.remove(m.name)
                    val snackbarBoughtWord = Snackbar.make(findViewById(R.id.map_Layout), "The Word You Bought Is: " + collectedLyric, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Open Lyrics List", View.OnClickListener() {
                                collectedLyricsCount = 0
                                Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                                alert(TextUtils.join(", ", collectedLyrics))
                                { title("Collected Lyrics") }.show()
                            })
                    snackbarBoughtWord.setActionTextColor(Color.WHITE)
                    snackbarBoughtWord.show()
                    lyricPoints = lyricPoints - wordCost
                }
            }
        } else {
            notEnoughPoints()
        }
    }

    fun buyArtist() {

        if (lyricPoints > 99){
            Snackbar.make(findViewById(R.id.map_Layout), "The Artists' Name is: " + artistsName, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Okay", View.OnClickListener() {})
                    .setActionTextColor(Color.WHITE)
                    .show()
            lyricPoints = lyricPoints - 100
        } else {
            notEnoughPoints()
        }

    }

    fun notEnoughPoints() {
        Snackbar.make(findViewById(R.id.map_Layout), "You Don't Have Enough Lyric Points", Snackbar.LENGTH_LONG)
                .setAction("Okay", View.OnClickListener() {})
                .setActionTextColor(Color.WHITE)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_submit)
        {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Guess The Song Title")

            val input = EditText(this)
            input.setInputType(InputType.TYPE_CLASS_TEXT)
            builder.setView(input)

            val ims = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ims.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            builder.setPositiveButton("Okay", DialogInterface.OnClickListener() { dialog, id ->
                ims.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                Log.v("dialog", input.text.toString())
                if (song.replace(" ", "").equals(input.text.toString().replace(" ", ""), ignoreCase = true)) {
                    switchToWin()
                }
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialog, id ->
                ims.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                dialog.cancel()
                toast("Sorry that was Incorrect")
            })

            builder.show()

            return true
        }
        if (id == R.id.action_hints)
        {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Purchase Hints For Lyric Points\nCurrent Lyric Points: " + lyricPoints)
            val listItems = ArrayList<String>()
            if (difficulty == 1) {
                listItems.add("Get a Word (25 Lyric Points)")
            }
            if (difficulty > 1) {
                listItems.add("Get a Boring Word (5 Lyric Points)")
                listItems.add("Get a Not Boring Word (10 Lyric Points)")
            }
            if (difficulty > 2) {
                listItems.add("Get an Interesting Word (20 Lyric Points)")
            }
            if (difficulty == 5) {
                listItems.add("Get a Very Interesting Word (50 Lyric Points)")
            }
            listItems.add("Get Artists' Name (100 Lyric Points)")

            builder.setItems(Array(listItems.size) { i -> listItems[i].toString()}) {dialog, which ->
                when (which) {
                    0 -> {
                        if (difficulty == 1) {
                            buyUnclassified()
                        }else {
                            buyClassified("boring", 5)
                        }
                    }
                    1 -> {
                        if (difficulty == 1) {
                            buyArtist()
                        } else {
                            buyClassified("notboring", 10)
                        }
                    }
                    2 -> {
                        if (difficulty == 2) {
                            buyArtist()
                        } else {
                            buyClassified("interesting", 20)
                        }
                    }
                    3 -> {
                        if (difficulty == 3 || difficulty == 4) {
                            buyArtist()
                        } else {
                            buyClassified("veryinteresting", 50)
                        }
                    }
                    4 -> {
                        buyArtist()
                    }
                }
            }

            builder.show()

            return true
        }

        if (id == R.id.action_lyrics) {
            collectedLyricsCount = 0
            Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
            alert(TextUtils.join(", ", collectedLyrics))
            {title("Collected Lyrics")}.show()
            return true
        }
        /*{ alert("You have unlocked the\n Walk 5 Kilometers Achievement\nHere are 10 Lyric Points") {
            title("Congratulations!")
            }.show()
            return true*/
            /*alert("You gained a total of 24 Lyric Points this Game\n\nScore: 248\n\nHigh Score: 600") {
                title("Congratulations. You Guessed Correctly!!")
                yesButton { toast("Go To Home Screen")}
            }.show()
            return true*/
            /*alert() {
                title("Sorry That Is Incorrect.")
                positiveButton("Keep Playing") {}
                negativeButton("Give Up") {}
            }.show()
            return true*/
            /*Snackbar.make(coordinatorLayout(), "The Artists Name Is: Queen", Snackbar.LENGTH_INDEFINITE).setAction("Okay", View.OnClickListener() {}).show()*/
            /*startActivity(Intent(this, WinActivity::class.java))
                return true*/
        if (id == R.id.action_newgame)
        { alert("Are you sure you want to start a new game? All progress will be lost!") {
            yesButton {
                switchToMode()
            }
            noButton {}
            }.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        songNumber = settings.getInt("storedSongNumber", 1)
        difficulty = settings.getInt("storedModeId", 1)
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("incorrectGuesses", incorrectGuesses)
        editor.putInt("lyricPoints", lyricPoints)
        editor.putString("collectedMarkers", collectedMarkers)
        // Apply the edits!
        editor.apply()
    }

    fun createLocationRequest() {
        //Set the parameters for the location request
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000 // preferably every 5 seconds
        mLocationRequest.fastestInterval = 1000 // at most every second
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Can we access the user’s current location?
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnected(connectionHint: Bundle?) {
        try {
            createLocationRequest();
        }
        catch (ise: IllegalStateException) {
            println("[$TAG] [onConnected] IllegalStateException thrown")
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionsRequestAccessFineLocation)
        }
    }

    override fun onLocationChanged(current: Location?) {
        if (current == null) {
            println("[onLocationChanged] Location unknown")
        } else {
            println("""[onLocationChanged] Lat/long now
            (${current.getLatitude()},
            ${current.getLongitude()})"""
            )
            // Do something with current location
            for (marker in uncollectedMarkersList) {
                val coords = marker.point.split(',')
                val result = FloatArray(10)
                Location.distanceBetween(current.latitude, current.longitude, coords[1].toDouble(), coords[0].toDouble(), result)
                if (result[0] < 10) {
                    val markerName = markerHash.get(marker.name)
                    if (markerName == null) {
                        //Do nothing
                    } else {
                        markerName.remove()
                        val points = marker.name.split(":")
                        val collectedLyric = lyrics.get(points[0].toInt()-1)[points[1].toInt()-1]
                        collectedLyrics.add(collectedLyric)
                        Log.v("lyrics", collectedLyric)
                        markerHash.remove(marker.name)
                        collectedMarkers = collectedMarkers + "," + marker.name
                        collectedLyricsCount++
                        lyricPoints++
                        val snackbarLyricCount = Snackbar.make(findViewById(R.id.map_Layout), "New Lyrics Collected: " + collectedLyricsCount, Snackbar.LENGTH_INDEFINITE)
                        snackbarLyricCount.setAction("Open Lyrics List", View.OnClickListener() {
                            collectedLyricsCount = 0
                            Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                            alert(TextUtils.join(", ", collectedLyrics))
                            {title("Collected Lyrics")}.show()
                        })
                        snackbarLyricCount.setActionTextColor(Color.WHITE)
                        snackbarLyricCount.show()
                    }
                }
            }
        }
    }

    override fun onConnectionSuspended(flag: Int) {
        println(" >>>> onConnectionSuspended")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        println(" >>>> onConnectionFailed")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(55.945, -3.1885)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.toFloat()))

        // Also available: new LatLngZoom(sydney, 15)

        try {
            // Visualise current position with a small blue circle
            mMap.isMyLocationEnabled = true
        } catch (se: SecurityException) {
            println("Security exception thrown [onMapReady]")
        }

        // Add "My location" button to the user interface
        mMap.uiSettings.isMyLocationButtonEnabled = true

        doAsync {
            //Reset the lists for the new lyrics and markers
            uncollectedMarkersList.clear()
            lyrics.clear()
            if (songNumber < 10) {
                DownloadKmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0$songNumber/map$difficulty.kml")
                DownloadLyricsTask().execute("https://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0$songNumber/lyrics.txt")
            } else {
                DownloadKmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$songNumber/map$difficulty.kml")
                DownloadLyricsTask().execute("https://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$songNumber/lyrics.txt")
            }

            uiThread {
                for (marker in uncollectedMarkersList) {
                    val coords = marker.point.split(',')
                    Log.v("coordes", marker.description)
                    when (marker.description) {
                        "unclassified" -> {
                            val temp = mMap.addMarker(MarkerOptions().position(LatLng(coords[1].toDouble(), coords[0].toDouble())).title(marker.name).icon(fromResource(R.drawable.mm_wht_blank_resized)))
                            markerHash.put(marker.name, temp)
                        }
                        "boring" -> {
                            val temp = mMap.addMarker(MarkerOptions().position(LatLng(coords[1].toDouble(), coords[0].toDouble())).title(marker.name).icon(fromResource(R.drawable.mm_ylw_blank_resized)))
                            markerHash.put(marker.name, temp)
                        }
                        "notboring" -> {
                            val temp = mMap.addMarker(MarkerOptions().position(LatLng(coords[1].toDouble(), coords[0].toDouble())).title(marker.name).icon(fromResource(R.drawable.mm_ylw_circle_resized)))
                            markerHash.put(marker.name, temp)
                        }
                        "interesting" -> {
                            val temp = mMap.addMarker(MarkerOptions().position(LatLng(coords[1].toDouble(), coords[0].toDouble())).title(marker.name).icon(fromResource(R.drawable.mm_orange_diamond_resized)))
                            markerHash.put(marker.name, temp)
                        }
                        "veryinteresting" -> {
                            val temp = mMap.addMarker(MarkerOptions().position(LatLng(coords[1].toDouble(), coords[0].toDouble())).title(marker.name).icon(fromResource(R.drawable.mm_red_stars_resized)))
                            markerHash.put(marker.name, temp)
                        }
                    }
                }
                collectPrevMarkers(collectedMarkers, markerHash, collectedLyrics)
            }
        }
    }
}

val uncollectedMarkersList = ArrayList<KmlMarkerParser.Marker>()

private var collectedMarkers = ""

val lyrics = ArrayList<List<String>>()

fun collectPrevMarkers(collectedMarkers:String, markerHash:HashMap<String, Marker>, collectedLyrics:ArrayList<String>) {
    Log.v("collected", "working!")
    val prevMarkerKeys = collectedMarkers.split(",")
    for (markerKey in prevMarkerKeys) {
        val markerName = markerHash.get(markerKey)
        Log.v("collected", markerName.toString())
        var count = 0
        if (markerName == null) {
            //Do nothing
        } else {
            markerName.remove()
            Log.v("collected", "working?")
            val points = markerKey.split(":")
            val collectedLyric = lyrics.get(points[0].toInt()-1)[points[1].toInt()-1]
            collectedLyrics.add(collectedLyric)
            Log.v("lyrics", collectedLyric)
            markerHash.remove(markerKey)
        }
    }
}
