package com.example.shelfie

data class Book(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    var isFavorite: Boolean = true
)
