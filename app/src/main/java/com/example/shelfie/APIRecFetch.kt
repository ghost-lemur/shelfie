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

    fun getBookRecs(liked: Array<Book>, disliked: Array<Book>) {
        // Initialize OkHttp client
        val client = OkHttpClient()

        val apiKey = activity.assets.open("api-key.txt").bufferedReader().use { it.readText() }

        //format liked and disliked arrays into strings for input
        var likedString = "Liked:\n"
        for (book in liked) {
            likedString += "[[\"${book.title}\", \"${book.author}\", \"${book.description}\"]], "
        }
        likedString = likedString.dropLast(2) + "\n"
        var dislikedString = "Disliked:\n"
        for (book in disliked) {
            dislikedString += "[[\"${book.title}\", \"${book.author}\", \"${book.description}\"]], "
        }
        dislikedString = dislikedString.dropLast(2) + "\n"

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
                mapOf("role" to "user", "content" to (likedString + dislikedString))
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

                            try {
                                val jsonResponse = JsonParser.parseString(responseText).asJsonObject
                                // The OpenAI API response structure is different - we need to parse the content
                                val choices = jsonResponse.getAsJsonArray("choices")
                                val firstChoice = choices[0].asJsonObject
                                val messageContent = firstChoice.getAsJsonObject("message").get("content").asString

                                // Now parse the actual book recommendations from the message content
                                val booksJson = JsonParser.parseString(messageContent).asJsonObject
                                val booksJsonArray = booksJson.getAsJsonArray("books")

                                // Convert JSON array to a list of Book objects
                                val bookList = booksJsonArray.map { bookJson: JsonElement ->
                                    val bookMap = Gson().fromJson(bookJson, Map::class.java) as Map<String, Any?>
                                    Book.fromMap(bookMap)
                                }

                                for(int in bookList) {
                                    println(int.title)
                                }

                                // Append each book to the queue in MainActivity
                                (activity as? MainActivity)?.bookReturn?.addAll(bookList)
                                (activity as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
                                    (activity as? MainActivity)?.loadQueue()
                                }
                            } catch (e: Exception) {
                                println("Error parsing response: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                    } else {
                        activity.runOnUiThread {
                            println("Failed to get response: ${response.code}")
                            println("API key: $apiKey")
                        }
                    }
                }
            }
        })
    }
}
