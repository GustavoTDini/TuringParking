package com.example.turingparking.network

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class HttpRequestHelpers{

    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?, method: String): String {
        var jsonResponse = ""
        if (url == null) {
            return jsonResponse
        }
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = method
            urlConnection.readTimeout = SET_READ_TIMEOUT_IN_MILISSECONDS
            urlConnection.connectTimeout = SET_CONNECT_TIMEOUT_IN_MILISSECONDS
            urlConnection.connect()
            if (urlConnection.responseCode == URL_CONNECTION_OK_RESPONSE_CODE) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            Log.e(TAG, " Problem in makeHttpRequest $e")
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    companion object {

        private const val TAG = "HttpRequest"
        private const val SET_READ_TIMEOUT_IN_MILISSECONDS = 10000
        private const val SET_CONNECT_TIMEOUT_IN_MILISSECONDS = 15000
        private const val URL_CONNECTION_OK_RESPONSE_CODE = 200



    }

}