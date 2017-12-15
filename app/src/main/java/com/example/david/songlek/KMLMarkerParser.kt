package com.example.david.songlek

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class KmlMarkerParser() {

    //Marker class
    data class Marker(val name: String, val description: String, val styleUrl: String, val point: String)

    // We don’t use namespaces
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: InputStream): List<Marker> {
        input.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    false)
            parser.setInput(input, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Marker> {
        val markers = ArrayList<Marker>()
        parser.require(XmlPullParser.START_TAG, ns, "kml")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "Document") {
                markers.add(readDocument(parser))
            } else if (parser.name == "Placemark") {
                markers.add(readPlacemark(parser))
            } else {
                skip(parser)
            }
        }
        return markers
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDocument(parser: XmlPullParser): Marker {
        parser.require(XmlPullParser.START_TAG, ns, "Document")
        var name = ""
        var description = ""
        var styleUrl = ""
        var Point = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue
            if (parser.name == "Placemark")
                continue
            when (parser.name) {
                "name" -> name = readName(parser)
                "description" -> description = readDescription(parser)
                "styleUrl" -> styleUrl = readStyleUrl(parser)
                "Point" -> Point = readPoint(parser)
                else -> skip(parser)
            }
        }
        return Marker(name, description, styleUrl, Point)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPlacemark(parser: XmlPullParser): Marker {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark")
        var name = ""
        var description = ""
        var styleUrl = ""
        var Point = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue
            if (parser.name == "Placemark")
                continue
            when (parser.name) {
                "name" -> name = readName(parser)
                "description" -> description = readDescription(parser)
                "styleUrl" -> styleUrl = readStyleUrl(parser)
                "Point" -> Point = readPoint(parser)
                else -> skip(parser)
            }
        }
        return Marker(name, description, styleUrl, Point)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "name")
        return name
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "description")
        val description = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "description")
        return description
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPoint(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "Point")
        var coordinates = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val tagName = parser.name
            if (tagName == "coordinates") {
                coordinates = readCoordinates(parser)
            } else {
                skip(parser)
            }
        }
        return coordinates
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readCoordinates(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "coordinates")
        val coordinates = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "coordinates")
        return coordinates
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStyleUrl(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl")
        val styleUrl = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl")
        return styleUrl
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}