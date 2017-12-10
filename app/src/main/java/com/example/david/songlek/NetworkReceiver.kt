package com.example.david.songlek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import org.jetbrains.anko.doAsync

/**
 * Created by David on 09/12/2017.
 */
class NetworkReceiver : BroadcastReceiver() {
    var connectedToInternet = false

    override fun onReceive(context: Context, intent: Intent) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        if (networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
            // Wi´Fi is connected, so use Wi´Fi
            Log.v("connectoe", "connection")
            connectedToInternet = true
            if (songsList.size == 0) {
                doAsync {
                    DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                }
            }
        } else if (networkInfo != null) {
            // Have a network connection and permission, so use data
            Log.v("connectoe", "connections")
            connectedToInternet = true
            if (songsList.size == 0) {
                doAsync {
                    DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/cslp/data/songs/songs.xml")
                }
            }
        } else {
            // No Wi´Fi and no network connection
            Log.v("connectoe", "connectionz")
            connectedToInternet = false
        }
    }
}