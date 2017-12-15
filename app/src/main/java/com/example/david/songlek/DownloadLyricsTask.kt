package com.example.david.songlek

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadLyricsTask() {

    fun execute(vararg urls: String) {
        try {
            loadLyricsFromNetwork(urls[0])
        } catch (e: IOException) {
            println("<<<< Unable to load content. Check your network connection")
        }
    }

    private fun loadLyricsFromNetwork(urlString: String)  {
        val url = URL(urlString)
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        var line = reader.readLine()
        //add each lyric to the lyrics line, where the lyrics consist of a set of lyrics lines
        while (line!=null) {
            val lyricsLine = line.replace(",", "").replace(".", "").replace("!", "").replace("?", "").replace("(", "").replace(")", "").split(" ")//do not add these special chars to my lyrics (some special chars like apostrophe want to be preserved
            lyrics.add(lyricsLine)
            Log.v("working", lyricsLine[0])
            line = reader.readLine()
        }
        reader.close()
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

