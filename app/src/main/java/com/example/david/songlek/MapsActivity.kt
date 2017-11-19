package com.example.david.songlek

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
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
import org.jetbrains.anko.*
import java.io.File
import java.io.FileInputStream
import java.net.URL
import com.google.maps.android.data.kml.KmlLayer
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.design.coordinatorLayout

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var mLocationPermissionGranted = false
    private var mLastLocation: Location? = null
    val TAG = "MapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_submit)
        {
                val builder = AlertDialog.Builder(this)
                val input = EditText(this)
                input.setInputType(InputType.TYPE_CLASS_TEXT)
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                builder.setView(input)
                builder.setMessage("Guess The Song Title")
                        .setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, id ->
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)}).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)})
                builder.show()
            return true
        }
        if (id == R.id.action_hints)
        { alert("Current Lyric Points: 16\n\nGet A Boring Word (5 Lyric Points)\n\nGet A Not Boring Word (10 Lyric Points)\n\nGet An Interesting Word (20 Lyric Points)\n\nGet A Very Interesting Word (50 Lyric Points)\n\nGet Artists' Name (100 Lyric Points)") {
            title("Purchase Hints For Lyric Points")
        }.show()
            return true }
        if (id == R.id.action_lyrics)
        { alert("You have unlocked the\n Walk 5 Kilometers Achievement\nHere are 10 Lyric Points") {
            title("Congratulations!")
            }.show()
            return true
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

        }
        if (id == R.id.action_newgame)
        { alert("Are you sure you want to start a new game? All progress will be lost!") {
            yesButton { toast("Yes") }
            noButton { toast("No") }
            }.show()
            return true
        }
        return super.onOptionsItemSelected(item)
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
            createLocationRequest(); } catch (ise: IllegalStateException) {
            println("IllegalStateException thrown [onConnected]")
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
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
        }
        // Do something with current location
        // ...
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

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
            val difficulty = "4"
            val songNum = "01"
            val url = URL("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/$songNum/map$difficulty.kml")

            val kmlInputStream = url.openStream()
            val layer = KmlLayer(mMap, kmlInputStream, applicationContext)
            uiThread {
                layer.addLayerToMap()
            }
        }
    }

}

