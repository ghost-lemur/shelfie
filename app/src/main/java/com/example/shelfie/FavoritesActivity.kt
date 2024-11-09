package com.example.shelfie

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat


data class DummyBook(
    val title: String,
    val description: String
)

class FavoritesActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeButton: ImageButton
    private lateinit var profileButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        initializeViews()
        setupClickListeners()
        setupDummyData()
    }


    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)

        // Set up RecyclerView with grid layout
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        homeButton.setOnClickListener {
            // Navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Profile button is disabled since we're already in Favorites
        profileButton.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
        homeButton.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    private fun setupDummyData() {
        // Create multiple dummy books
        val dummyBooks = listOf(
            DummyBook(
                "The Great Adventure",
                "A thrilling journey through unknown lands..."
            ),
            DummyBook(
                "Mystery House",
                "An intriguing tale of secrets and mysteries..."
            ),
            DummyBook(
                "Tech Future",
                "Exploring the boundaries of technology..."
            ),
            DummyBook(
                "Coffee Tales",
                "Stories that brew perfect moments..."
            )
        )

        // Set adapter with dummy data
        val adapter = FavoritesAdapter(dummyBooks)
        recyclerView.adapter = adapter
    }
}
