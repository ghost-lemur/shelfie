package com.example.shelfie

data class Book(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    var isFavorite: Boolean = true
) {
    // Secondary constructor without imageUrl and isFavorite
    constructor(id: String, title: String, description: String) :
            this(id, title, description, null, true)

    // Secondary constructor without isFavorite
    constructor(id: String, title: String, description: String, imageUrl: String?) :
            this(id, title, description, imageUrl, true)

    companion object {
        // Create a book from a map/dictionary
        fun fromMap(map: Map<String, Any?>): Book {
            return Book(
                id = map["id"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String,
                isFavorite = map["isFavorite"] as? Boolean ?: true
            )
        }

        // Create an empty book
        fun empty(): Book {
            return Book("", "", "")
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
            "description" to description,
            "imageUrl" to imageUrl,
            "isFavorite" to isFavorite
        )
    }

    // Create a copy with updated favorite status
    fun toggleFavorite(): Book {
        return copy(isFavorite = !isFavorite)
    }

    // Check if book has valid required fields
    fun isValid(): Boolean {
        return id.isNotEmpty() && title.isNotEmpty() && description.isNotEmpty()
    }
}


// Usage examples:
/*
// Create books in different ways
val book1 = Book("1", "Title 1", "Description 1")
val book2 = Book("2", "Title 2", "Description 2", "http://image.url")
val book3 = Book("3", "Title 3", "Description 3", "http://image.url", false)

// Create from a map
val bookMap = mapOf(
    "id" to "4",
    "title" to "Title 4",
    "description" to "Description 4"
)
val book4 = Book.fromMap(bookMap)

// Create a list of books
val bookList = listOf(book1, book2, book3, book4)

// Convert list to array
val bookArray = bookList.toTypedArray()

// Create from a list of maps (useful for API responses)
val apiResponse = listOf(
    mapOf("id" to "5", "title" to "Title 5", "description" to "Description 5"),
    mapOf("id" to "6", "title" to "Title 6", "description" to "Description 6")
)
val books = Book.fromMapList(apiResponse)

// Filter favorite books
val favoriteBooks = bookList.filter { it.isFavorite }

// Sort books by title
val sortedBooks = bookList.sortedBy { it.title }

// Find a book by id
val foundBook = bookList.find { it.id == "1" }

// Update a book's favorite status
val updatedBook = book1.toggleFavorite()
*/