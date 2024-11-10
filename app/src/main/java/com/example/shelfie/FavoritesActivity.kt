package com.example.shelfie

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat

class FavoritesActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var adapter: FavoritesAdapter
    private lateinit var prefsManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        prefsManager = SharedPreferencesManager(this)
        initializeViews()
        setupClickListeners()
        loadFavorites()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)

        adapter = FavoritesAdapter()
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        profileButton.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
        homeButton.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    private fun loadFavorites() {
        val favoriteBooks = prefsManager.getLikedBooks()
        adapter.updateBooks(favoriteBooks)
    }

    override fun onResume() {
        super.onResume()
        loadFavorites() // Reload favorites when returning to this screen
    }
}