package com.example.shelfie

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object CoverJPG {
    @JvmStatic

/*
    fun main(args: Array<String>) {
        val bookTitle = "To Kill a Mockingbird"
        val returnUrl = searchBookByTitle(bookTitle)
        println(returnUrl)
    }
*/

    private fun extractCoverIdFromString(input: String): String? {
        // Find the index of the key "cover_i"
        val key = "\"cover_i\": "
        val startIndex = input.indexOf(key)

        if (startIndex != -1) {
            // Move the index to the start of the number
            val numberStartIndex = startIndex + key.length

            // Find the end of the number (where the next comma or closing brace is)
            val remaining = input.drop(startIndex)
            val numberEndIndex = remaining.indexOfFirst { it == ',' || it == '}' || it == ' ' || it == '\n' } - 2

            // Extract the substring containing the number
            val coverId = input.substring(numberStartIndex, numberStartIndex + numberEndIndex).trim()
            return coverId
        }
        return null
    }


    fun searchBookByTitle(title: String): String? {
        try {
            // URL encode the book title
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val apiUrl = "https://openlibrary.org/search.json?title=$encodedTitle"

            // Create a connection
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Get the response code
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = StringBuilder()

                // Read the response line by line
                while ((`in`.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                `in`.close()

                val coverID = extractCoverIdFromString(response.toString())
                println(coverID)
                val size = "M" //size image Medium

                val coverUrl = "https://covers.openlibrary.org/b/id/$coverID-$size.jpg"
                return coverUrl

            } else {
                println("GET request failed. Response Code: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}