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
import android.support.v4.content.PermissionChecker
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
import java.util.*
import kotlin.collections.ArrayList

val uncollectedMarkersList = ArrayList<KmlMarkerParser.Marker>()
val lyrics = ArrayList<List<String>>()

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //Initialise Variables
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val permissionsRequestAccessFineLocation = 1
    private var mLastLocation: Location? = null
    private val TAG = "MapsActivity"
    private val markerHash = HashMap<String, Marker>()
    private var collectedLyricsCount = 0
    private val collectedLyrics = ArrayList<String>()
    private var previousLocation: Location? = null
    private var startTime : Long = 0

    //Initialise sharedpreferences
    val PREFS_FILE = "MyPrefsFile" // for storing preferences
    private var colourId = 0
    private var difficulty = 1
    private var score = 0
    private var incorrectGuesses = 0
    private var lyricPoints = 100
    private var totalLyricPoints = 100
    private var lyricPointsEarned = 0
    private var newGame = true
    private var currentSongNumber = 1
    private var currentSongName = ""
    private var currentSongArtist = ""
    private var unlockedSongNumbers = ""
    private var totalDistanceTravelled = 0
    private var totalTimePlayed : Long = 0
    private var collectedMarkers = ""

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
        newGame = true

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


        //If not a duplicate add the songs number in the songs list, if it is a duplicate add -1
        unlockedSongNumbers = settings.getString("unlockedSongNumbers", "")
        val unlockedSongsList = unlockedSongNumbers.split(",")
        for (song in unlockedSongsList) {
            if (currentSongNumber.toString() == song) {
                currentSongNumber=-1 //Set it to an int we won't get in our list
            }
        }
        unlockedSongNumbers += "," + currentSongNumber.toString()

        //Save values in shared preferences
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("score", score)
        editor.putBoolean("newGame", newGame)
        editor.putString("unlockedSongNumbers", unlockedSongNumbers)
        // Apply the edits!
        editor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

        //get previously collected markers if not a new game
        newGame = settings.getBoolean("newGame", true)
        if (!newGame) {
            collectedMarkers = settings.getString("collectedMarkers", "")
            Log.d("collected", collectedMarkers)
        }

        //Now newGame is false unless we start a new game
        newGame = false
        //Save shared preferences
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("score", score)
        editor.putBoolean("newGame", newGame)
        // Apply the edits!
        editor.apply()

        //Get sharedpreferences
        lyricPoints = settings.getInt("lyricPoints", 100)
        totalLyricPoints = settings.getInt("totalLyricPoints", 100)
        currentSongNumber = settings.getInt("currentSongNumber", 1)
        currentSongName = settings.getString("currentSongName", "")
        currentSongArtist = settings.getString("currentSongArtist", "")

        //Set correct theme colour
        colourId = settings.getInt("storedColourId", 0)
        when (colourId) {
            0 -> setTheme(R.style.RedTheme)
            1 -> setTheme(R.style.BlueTheme)
            2 -> setTheme(R.style.GreenTheme)
            3 -> setTheme(R.style.PurpleTheme)
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

    //Stops going back to mode (user must press new game button or win the game)
    override fun onBackPressed() {
        switchToMain()
    }

    //Buy unclassified lyric (Extreme Difficulty only)
    private fun buyUnclassified() {
        if (lyricPoints > 24) {
            val marker = uncollectedMarkersList[0]
            uncollectedMarkersList.remove(marker)
            collectedMarkers = collectedMarkers + "," + marker.name
            val markerName = markerHash[marker.name]
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
                        .setAction("Open Lyrics List", View.OnClickListener {
                            collectedLyricsCount = 0
                            Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                            alert(TextUtils.join(", ", collectedLyrics))
                            { title("Collected Lyrics") }.show()
                        })
                snackbarBoughtWord.setActionTextColor(Color.WHITE)
                snackbarBoughtWord.show()
                lyricPoints -= 25
            }
        } else {
            notEnoughPoints()
        }
    }

    // Buy classified lyric (easy to very hard difficulty only)
    private fun buyClassified(string:String, wordCost:Int) {
        if (lyricPoints > wordCost-1) {
            var m = uncollectedMarkersList[0]

            //breaks if requested word is found
            loop@ for (marker in uncollectedMarkersList) {
                m = marker
                if (marker.description == string) break@loop
            }

            //Checks if m is not of the requested description
            if (m.description != string) {
                val snackbarBoughtWord = Snackbar.make(findViewById(R.id.map_Layout), "There Are No " + string + "Left to Buy", Snackbar.LENGTH_LONG)
                    .setAction("Open Lyrics List", View.OnClickListener {
                        Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                        alert(TextUtils.join(", ", collectedLyrics))
                        { title("Collected Lyrics") }.show()
                    })
            snackbarBoughtWord.setActionTextColor(Color.WHITE)
            snackbarBoughtWord.show()
            } else {
                uncollectedMarkersList.remove(m)
                collectedMarkers = collectedMarkers + "," + m.name
                val markerName = markerHash[m.name]
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
                            .setAction("Open Lyrics List", View.OnClickListener {
                                collectedLyricsCount = 0
                                Snackbar.make(findViewById(R.id.map_Layout), "", 1).show()
                                alert(TextUtils.join(", ", collectedLyrics))
                                { title("Collected Lyrics") }.show()
                            })
                    snackbarBoughtWord.setActionTextColor(Color.WHITE)
                    snackbarBoughtWord.show()
                    lyricPoints -= wordCost
                }
            }
        } else {
            notEnoughPoints()
        }
    }

    // Buy artists name
    private fun buyArtist() {
        if (lyricPoints > 99){
            Snackbar.make(findViewById(R.id.map_Layout), "The Artists' Name is: " + currentSongArtist, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Okay", View.OnClickListener {})
                    .setActionTextColor(Color.WHITE)
                    .show()
            lyricPoints -= 100
        } else {
            notEnoughPoints()
        }

    }

    // Called if the user doesn't have enough LyricPoints for their requested hint
    private fun notEnoughPoints() {
        Snackbar.make(findViewById(R.id.map_Layout), "You Don't Have Enough Lyric Points", Snackbar.LENGTH_LONG)
                .setAction("Okay", View.OnClickListener {})
                .setActionTextColor(Color.WHITE)
                .show()
    }

    //This function adds the 4 buttons to the top right of our MapsActivity
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

            builder.setPositiveButton("Okay", DialogInterface.OnClickListener() { dialog, _ ->
                ims.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                Log.v("dialog", input.text.toString())
                val keepChars = Regex("[^A-Za-z0-9]")
                if (keepChars.replace(currentSongName, "").equals(keepChars.replace(input.text.toString(), ""), ignoreCase = true)) {
                    switchToWin()
                } else {
                    toast("Sorry that was Incorrect")
                }
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialog, _ ->
                ims.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                dialog.cancel()
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

            builder.setItems(Array(listItems.size) { i -> listItems[i]}) {_, which ->
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
        if (id == R.id.action_newgame)
        { alert("Are you sure you want to start a new game? All progress will be lost!") {
            yesButton {
                newGame = true
                val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
                // We need an Editor object to make preference changes.
                val editor = settings.edit()
                editor.putBoolean("newGame", newGame)
                // Apply the edits!
                editor.apply()
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

        //Set values required for our shared preferences
        startTime = System.currentTimeMillis()

        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // use 1 as the default value (this might be the first time the app is run)
        currentSongNumber = settings.getInt("currentSongNumber", 1)
        difficulty = settings.getInt("storedModeId", 1)
        totalDistanceTravelled = settings.getInt("totalDistanceTravelled", 0)
        Log.d("totalDistanceTravelled", totalDistanceTravelled.toString())
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

        //Save sharedpreferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        totalTimePlayed = settings.getLong("totalTimePlayed", totalTimePlayed)
        totalTimePlayed += (System.currentTimeMillis() - startTime)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("incorrectGuesses", incorrectGuesses)
        editor.putInt("lyricPoints", lyricPoints)
        editor.putInt("totalLyricPoints", totalLyricPoints)
        editor.putInt("totalDistanceTravelled", totalDistanceTravelled)
        editor.putLong("totalTimePlayed", totalTimePlayed)
        editor.putString("collectedMarkers", collectedMarkers)
        // Apply the edits!
        editor.apply()

        Log.d("totalTimePlayed", totalTimePlayed.toString())

    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
        //Save sharedpreferences
        val settings = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        // We need an Editor object to make preference changes.
        val editor = settings.edit()
        editor.putInt("lyricPointsEarned", lyricPointsEarned)
        // Apply the edits!
        editor.apply()
    }

    private fun createLocationRequest() {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //If the user denies permissions we got back to the main menu
        if (grantResults.contains(PermissionChecker.PERMISSION_DENIED)) {
            switchToMain()
            longToast("Songle Requires Location Permissions to be Played")
        } else {
            //These lines restart the MapsActivity after permissions are granted, so that the location marker and button appear.
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }
    }

    override fun onConnected(connectionHint: Bundle?) {
        try {
            createLocationRequest()
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
            //Calculate Distance Travelled between last location change
            if (previousLocation != null) {
                totalDistanceTravelled += current.distanceTo(previousLocation).toInt()
            }

            // Remove all markers within 10m of the location and add them to the lyrics list
            for (marker in uncollectedMarkersList) {
                val coords = marker.point.split(',')
                val result = FloatArray(10)
                Location.distanceBetween(current.latitude, current.longitude, coords[1].toDouble(), coords[0].toDouble(), result)
                if (result[0] < 10) {
                    val markerName = markerHash[marker.name]
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
                        totalLyricPoints++
                        lyricPointsEarned++
                        val snackbarLyricCount = Snackbar.make(findViewById(R.id.map_Layout), "New Lyrics Collected: " + collectedLyricsCount, Snackbar.LENGTH_INDEFINITE)
                        snackbarLyricCount.setAction("Open Lyrics List", View.OnClickListener {
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
            previousLocation = current
        }
    }

    override fun onConnectionSuspended(flag: Int) {
        println(" >>>> onConnectionSuspended")
        longToast("Please Check Your Network Connection")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        println(" >>>> onConnectionFailed")
        longToast("A Connection to Google Could Not Be Established.\nTry Checking Your Connection & Reloading Songle")
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

        // Add a marker in Edinburgh and move the camera
        val edinburgh = LatLng(55.945, -3.1885)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edinburgh, 16.toFloat()))

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
            if (currentSongNumber < 10) {
                DownloadKmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0$currentSongNumber/map$difficulty.kml")
                DownloadLyricsTask().execute("https://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/0$currentSongNumber/lyrics.txt")
            } else {
                DownloadKmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$currentSongNumber/map$difficulty.kml")
                DownloadLyricsTask().execute("https://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$currentSongNumber/lyrics.txt")
            }

            uiThread {
                //add all markers to the map once they've been parsed from the KML file
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
                collectPrevMarkers(collectedMarkers, markerHash, collectedLyrics, lyrics)
            }
        }
    }
}

//collect markers from the map that were collected in a previous session of the same game
fun collectPrevMarkers(collectedMarkers:String, markerHash:HashMap<String, Marker>, collectedLyrics:ArrayList<String>, lyrics:ArrayList<List<String>>) {
    Log.v("collected", "working!")
    val prevMarkerKeys = collectedMarkers.split(",")
    for (markerKey in prevMarkerKeys) {
        val markerName = markerHash[markerKey]
        Log.v("collected", markerName.toString())
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

