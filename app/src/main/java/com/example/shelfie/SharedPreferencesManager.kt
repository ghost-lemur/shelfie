package com.example.shelfie

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ShelfiePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_LIKED_BOOKS = "liked_books"
        private const val KEY_SHOWN_BOOKS = "shown_books"
    }

    fun saveLikedBook(book: Book) {
        val likedBooks = getLikedBooks().toMutableList()
        likedBooks.add(book)
        val json = gson.toJson(likedBooks)
        sharedPreferences.edit().putString(KEY_LIKED_BOOKS, json).apply()
    }

    fun getLikedBooks(): List<Book> {
        val json = sharedPreferences.getString(KEY_LIKED_BOOKS, "[]")
        val type = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addShownBook(book: Book) {
        val shownBooks = getShownBooks().toMutableList()
        shownBooks.add(book)
        val json = gson.toJson(shownBooks)
        sharedPreferences.edit().putString(KEY_SHOWN_BOOKS, json).apply()
    }

    fun getShownBooks(): List<Book> {
        val json = sharedPreferences.getString(KEY_SHOWN_BOOKS, "[]")
        val type = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearShownBooks() {
        sharedPreferences.edit().remove(KEY_SHOWN_BOOKS).apply()
    }
}