package com.example.shelfie

import android.app.Activity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.*
import java.io.IOException
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class APIRecFetch(private val activity: Activity) {
    interface BookRecommendationCallback {
        fun onSuccess(books: List<Book>)
        fun onError(error: String)
    }

    fun getBookRecs(liked: Array<Book>, disliked: Array<Book>, recentGens: List<Book>, callback: BookRecommendationCallback) {
        // Get shown books from SharedPreferences
        val prefsManager = SharedPreferencesManager(activity)

        val client = OkHttpClient()
        val apiKey = activity.assets.open("api-key.txt").bufferedReader().use { it.readText() }

        var likedString = "Recently Liked:\n["
        for (book in liked) {
            likedString += "[\"${book.title}\", \"${book.author}\"], "
        }
        likedString = likedString.dropLast(2) + "]\n"

        var dislikedString = "Recently Disliked:\n["
        for (book in disliked) {
            dislikedString += "[\"${book.title}\", \"${book.author}\"], "
        }
        dislikedString = dislikedString.dropLast(2) + "]\n"

        var previouslyShownString = "All Shown (do NOT recommend):\n["
        for (book in recentGens) {
            previouslyShownString += "\"${book.title}\", "
        }
        previouslyShownString = previouslyShownString.dropLast(2) + "]\n"

        val systemMessage = """
            You are a backend AI that recommends exactly five books based on a user's preferences, without repeats. The user will provide three lists: one containing books they recently liked, one containing books they recently disliked, and one containing all books they've read. Your job is to construct a JSON array of five new books (along with their authors and a short 1-2 sentence summary) you would recommend them in the following format:
            {
              "books": [
                {
                  "title": "TITLE_1",
                  "author": "AUTHOR_1",
                  "description": "SUMMARY_1"
                },
                {
                  "title": "TITLE_2",
                  "author": "AUTHOR_2",
                  "description": "SUMMARY_2"
                }
              }
            }
            Here is an example of a user prompt followed by an example response:
            User:
            Recently Liked: [["Pride and Prejudice", "Jane Austen"], ["Hyperbole and a Half", "Allie Brosh"], ["Ooka the Wise", "L. G. Edmonds"]]
            Recently Disliked: [["Unbelievable", "Katy Tur"], ["The Moscow Puzzles", "Boris A. Kordemsky"]]
            All Shown: ["Pride and Prejudice", "Hyperbole and a Half", "Unbelievable", "The"Les Miserables", "Ooka the Wise", "Adventures of Huckleberry Finn", "The Great Gatsby", The Moscow Puzzles]
            AI: {
              "books": [
                {
                  "title": "Emma",
                  "author": "Jane Austen",
                  "description": "A clever, matchmaking heroine learns life's lessons on love and self-awareness in this comedic and satirical social drama."
                },
                {
                  "title": "Furiously Happy",
                  "author": "Jenny Lawson",
                  "description": "A humor-filled exploration of mental health that embraces the absurd and celebrates resilience, similar in style to Brosh's candid approach."
                },
                {
                  "title": "Anne of Green Gables",
                  "author": "L.M. Montgomery",
                  "description": "The adventures of imaginative orphan Anne Shirley bring warmth and wit to themes of belonging and self-discovery."
                },
                {
                  "title": "The Tale of the Mandarin Ducks",
                  "author": "Katherine Paterson",
                  "description": "This beautifully illustrated folktale shares Ooka's sense of wisdom through a story of kindness and justice in feudal Japan."
                },
                {
                  "title": "The Princess Bride",
                  "author": "William Goldman",
                  "description": "A humorous and adventurous fairytale with romance, wit, and a self-aware twist that balances light-heartedness with clever storytelling."
                }
              ]
            }
            Please adhere only to this format and don't add any extra text to your response.
            Book recommendations should be creative and unique that the user hasn't seen before.
            Your descriptions should vary in sentence structure, capturing the essence of the plot and highlighting which themes might interest the reader.
            Make sure to vary the descriptions so they don't get boring.
            Each recommended book must be different from any in the "Previously shown" list.
            You should strongly consider what the user likes and dislikes when deciding recommendations.
            Keep in mind the user's genre preferences. Suggest a mix of popular and niche titles.
            Note that you are given only recently liked and disliked books.
            Try to match the user's taste but change it up every so often.
            Offer the user new avenues to explore based on their tastes.
            Once again, no duplicates. The list can be long, but do not suggest books already in the list.
        """.trimIndent()

        val jsonBody = JsonObject().apply {
            addProperty("model", "gpt-3.5-turbo")
            add("messages", Gson().toJsonTree(listOf(
                mapOf("role" to "system", "content" to systemMessage),
                mapOf("role" to "user", "content" to (likedString + dislikedString + previouslyShownString))
            )))
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread {
                    callback.onError("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseText = it.body?.string()
                        activity.runOnUiThread {
                            try {
                                val jsonResponse = JsonParser.parseString(responseText).asJsonObject
                                val choices = jsonResponse.getAsJsonArray("choices")
                                val firstChoice = choices[0].asJsonObject
                                val messageContent = firstChoice.getAsJsonObject("message").get("content").asString
                                val booksJson = JsonParser.parseString(messageContent).asJsonObject
                                val booksJsonArray = booksJson.getAsJsonArray("books")

                                val bookList = booksJsonArray.map { bookJson: JsonElement ->
                                    val bookMap = Gson().fromJson(bookJson, Map::class.java) as Map<String, Any?>
                                    Book.fromMap(bookMap)
                                }

                                callback.onSuccess(bookList)
                            } catch (e: Exception) {
                                callback.onError("Error parsing response: ${e.message}")
                            }
                        }
                    } else {
                        activity.runOnUiThread {
                            callback.onError("Failed to get response: ${response.code}")
                        }
                    }
                }
            }
        })
    }
}