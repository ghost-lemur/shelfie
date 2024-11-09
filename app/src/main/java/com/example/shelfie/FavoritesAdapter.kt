package com.example.shelfie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(private val books: List<DummyBook>) :
    RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookImage)
        val bookTitle: TextView = view.findViewById(R.id.bookTitle)
        val bookDescription: TextView = view.findViewById(R.id.bookDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Double check that your CardView XML file is named "favorite_book_item.xml"
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_book_item, parent, false) // Make sure this matches your XML filename
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.bookImage.setImageResource(R.drawable.placeholder_book) // your placeholder image
        holder.bookTitle.text = book.title
        holder.bookDescription.text = book.description
    }

    override fun getItemCount() = books.size
}