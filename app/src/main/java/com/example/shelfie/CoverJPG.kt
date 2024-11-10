package com.example.shelfie

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoverJPG {
    private fun extractCoverIdFromString(input: String): String? {
        val regex = "\"cover_i\":\\s*(\\d+)"
        val matchResult = Regex(regex).find(input)
        return matchResult?.groups?.get(1)?.value
    }

    // Make the function a suspend function to use with coroutines
    suspend fun searchBookByTitle(title: String): String? = withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val apiUrl = "https://openlibrary.org/search.json?title=$encodedTitle"

            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()

                var inputLine: String?
                while ((`in`.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                `in`.close()

                val coverID = extractCoverIdFromString(response.toString())
                println(coverID)
                val size = "M" //size image Medium

                "https://covers.openlibrary.org/b/id/$coverID-$size.jpg"
            } else {
                println("GET request failed. Response Code: $responseCode")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}