package com.example.shelfie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation

class FavoritesAdapter(private var books: List<Book> = emptyList()) :
    RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookImage)
        val bookTitle: TextView = view.findViewById(R.id.bookTitle)
        val bookDescription: TextView = view.findViewById(R.id.bookDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_book_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]

        // Load cover image using Coil
        holder.bookImage.load(book.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder_book)
            error(R.drawable.placeholder_book)
            transformations(RoundedCornersTransformation(8f))
        }

        holder.bookTitle.text = book.title
        holder.bookDescription.text = book.description
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}