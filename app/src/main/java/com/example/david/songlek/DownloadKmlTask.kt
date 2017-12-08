package com.example.david.songlek

import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by David on 08/12/2017.
 */
class DownloadKmlTask() {

    fun execute(vararg urls: String): String {
        return try {
            loadKMLFromNetwork(urls[0])
        } catch (e: IOException) {
            "Unable to load content. Check your network connection"
        } catch (e: XmlPullParserException) {
            "Error parsing XML"
        }
    }

    private fun loadKMLFromNetwork(urlString: String): String  {
        val result = StringBuilder()
        val stream = downloadUrl(urlString)
        val parsedMarkers = KmlMarkerParser().parse(stream)
        result.append(parsedMarkers.toString())
        for (marker in parsedMarkers) {
            uncollectedMarkersList.add(marker)
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

}