package com.example.david.songlek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import org.jetbrains.anko.doAsync

class NetworkReceiver : BroadcastReceiver() {
    var connectedToInternet = false

    override fun onReceive(context: Context, intent: Intent) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        if (networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
            // Wi´Fi is connected, so use Wi´Fi
            //Log.d("connections", "Wifi connection")
            connectedToInternet = true
            //If songsList not downloaded
            if (songsList.size == 0) {
                doAsync {
                    DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                }
            }
        } else if (networkInfo != null) {
            // Have a network connection and permission, so use data
            //Log.d("connections", "Network connections")
            connectedToInternet = true
            //If songsList not downloaded
            if (songsList.size == 0) {
                doAsync {
                    DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                }
            }
        } else {
            // No Wi´Fi and no network connection
            //Log.d("connections", "No Connection")
            connectedToInternet = false
        }
    }
}