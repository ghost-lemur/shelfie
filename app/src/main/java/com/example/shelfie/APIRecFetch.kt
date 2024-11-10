package com.example.shelfie

import android.app.Activity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.IOException
import java.io.File

class APIRecFetch(private val activity: Activity) {

    fun getBookRecs(liked: Array<String>, disliked: Array<String>) {
        // Initialize OkHttp client
        val client = OkHttpClient()

        val apiKey = File("api-key.txt").readText()

        // Create requestBody from JSON
        val jsonBody = JsonObject().apply {
            addProperty("model", "gpt-3.5-turbo")
            add("messages", Gson().toJsonTree(listOf(
                mapOf("role" to "system", "content" to "You are a backend AI that recommends exactly five books based on a user's preferences. The user will provide two lists: one containing books they like, and one containing books they dislike. Your job is to construct a JSON array of five books (along with their authors and a short 1-2 sentence summary) you would recommend them in the following format:\n" +
                        "{\n" +
                        "  \"books\": [\n" +
                        "    {\n" +
                        "      \"title\": \"TITLE_1\",\n" +
                        "      \"author\": \"AUTHOR_1\",\n" +
                        "      \"description\": \"SUMMARY_1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"TITLE_2\",\n" +
                        "      \"author\": \"AUTHOR_2\",\n" +
                        "      \"description\": \"SUMMARY_2\"\n" +
                        "    }" +
                        "}\n" +
                        "Here is an example of a user prompt followed by an example response:\n" +
                        "User:\n" +
                        "Liked: [[\"Pride and Prejudice\", \"Jane Austen\", \"A witty exploration of social class and romantic misunderstandings in 19th-century England.\"], " +
                        "[\"Hyperbole and a Half\", \"Allie Brosh\", \"A collection of humorous and poignant illustrations and stories about life’s challenges and absurdities.\"], " +
                        "[\"Ooka the Wise\", \"L. G. Edmonds\", \"A Japanese folktale about a wise judge who resolves disputes with cleverness and fairness.\"]]\n" +
                        "Disliked: [[\"Unbelievable\", \"Katy Tur\", \"A memoir about the author’s experience covering the 2016 election, reflecting on truth and media integrity.\"], " +
                        "[\"The Moscow Puzzles\", \"Boris A. Kordemsky\", \"A collection of mathematical puzzles, often complex and dry in nature.\"]]\n" +
                        "AI: " +
                        "{\n" +
                        "  \"books\": [\n" +
                        "    {\n" +
                        "      \"title\": \"Emma\",\n" +
                        "      \"author\": \"Jane Austen\",\n" +
                        "      \"description\": \"A clever, matchmaking heroine learns life’s lessons on love and self-awareness in this comedic and satirical social drama.\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"Furiously Happy\",\n" +
                        "      \"author\": \"Jenny Lawson\",\n" +
                        "      \"description\": \"A humor-filled exploration of mental health that embraces the absurd and celebrates resilience, similar in style to Brosh's candid approach.\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"Anne of Green Gables\",\n" +
                        "      \"author\": \"L.M. Montgomery\",\n" +
                        "      \"description\": \"The adventures of imaginative orphan Anne Shirley bring warmth and wit to themes of belonging and self-discovery.\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"The Tale of the Mandarin Ducks\",\n" +
                        "      \"author\": \"Katherine Paterson\",\n" +
                        "      \"description\": \"This beautifully illustrated folktale shares Ooka’s sense of wisdom through a story of kindness and justice in feudal Japan.\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"The Princess Bride\",\n" +
                        "      \"author\": \"William Goldman\",\n" +
                        "      \"description\": \"A humorous and adventurous fairytale with romance, wit, and a self-aware twist that balances light-heartedness with clever storytelling.\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}" +
                        "Please adhere only to this format and don't add any extra text to your response. Book recommendations should be creative and unique, and should engage the user by supporting their preferences while encouraging them to explore new avenues."),
                mapOf("role" to "user", "content" to "Liked: " +
                        "[[\"The Hobbit\", \"J.R.R. Tolkien\", \"A fantasy adventure about Bilbo Baggins’ unexpected journey to reclaim a treasure from a dragon.\"], " +
                        "[\"Bossypants\", \"Tina Fey\", \"A comedic memoir filled with funny anecdotes from the author's life and career in entertainment.\"], " +
                        "[\"The Secret Garden\", \"Frances Hodgson Burnett\", \"A heartwarming story of a lonely girl who discovers the healing power of nature and friendship.\"]]\n" +
                        "Disliked: " +
                        "[[\"Fifty Shades of Grey\", \"E.L. James\", \"A controversial romance novel criticized for its portrayal of relationships and controversial themes.\"], " +
                        "[\"The Alchemist\", \"Paulo Coelho\", \"A philosophical novel about self-discovery that some find overly simplistic or preachy.\"]]")
            )))
        }
        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

        // Make a POST request
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        // Execute the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Use runOnUiThread to handle UI updates
                activity.runOnUiThread {
                    println("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseText = it.body?.string()
                        // Use runOnUiThread to handle UI updates
                        activity.runOnUiThread {
                            println("Response: $responseText")
                            // Update UI elements here, for example:
                            // textView.text = responseText
                        }
                    } else {
                        activity.runOnUiThread {
                            println("Failed to get response: ${response.code}")
                        }
                    }
                }
            }
        })
    }
}
