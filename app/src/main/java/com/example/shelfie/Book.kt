
package com.example.shelfie

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String? = null,
    var isFavorite: Boolean = true
) {
    // Secondary constructor without imageUrl and isFavorite
    constructor(id: String, title: String, author: String, description: String) :
            this(id, title, author, description, null, true)

    // Secondary constructor without isFavorite
    constructor(id: String, title: String, author: String, description: String, imageUrl: String?) :
            this(id, title, author, description, imageUrl, true)

    companion object {
        // Create a book from a map/dictionary
        fun fromMap(map: Map<String, Any?>): Book {
            return Book(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                author = map["author"] as? String ?: "",
                description = map["description"] as? String ?: "",
                coverUrl = map["imageUrl"] as? String,
                isFavorite = map["isFavorite"] as? Boolean ?: true
            )
        }

        // Create an empty book
        fun empty(): Book {
            return Book("", "", "", "")
        }

        // Create a list of books from a list of maps
        fun fromMapList(list: List<Map<String, Any?>>): List<Book> {
            return list.map { fromMap(it) }
        }
    }

    // Convert book to map/dictionary
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "author" to author,
            "description" to description,
            "imageUrl" to coverUrl,
            "isFavorite" to isFavorite
        )
    }

    // Create a copy with updated favorite status
    fun toggleFavorite(): Book {
        return copy(isFavorite = !isFavorite)
    }

    // Check if book has valid required fields
    fun isValid(): Boolean {
        return id.isNotEmpty() && title.isNotEmpty() && author.isNotEmpty() && description.isNotEmpty()
    }
}
