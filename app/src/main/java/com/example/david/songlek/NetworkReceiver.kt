package com.example.david.songlek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

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
        } else if (networkInfo != null) {
            // Have a network connection and permission, so use data
            Log.v("connectoe", "connections")
            connectedToInternet = true
        } else {
            // No Wi´Fi and no network connection
            Log.v("connectoe", "connectionz")
            connectedToInternet = false
        }
    }
}