package com.example.shelfie
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.View
import android.widget.TextView
import kotlin.math.abs
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var bookImageView: ImageView
    private lateinit var heartButton: ImageButton
    private lateinit var recycleButton: ImageButton
    private lateinit var titleText: TextView
    private lateinit var homeButton: ImageButton  // Added type
    private lateinit var profileButton: ImageButton  // Added type
    private lateinit var gestureDetector: GestureDetector
    private lateinit var bookDescription: TextView

    // Sample list of book covers and titles
    private val bookCovers = listOf(
        R.drawable.placeholder_book,
        // Add more book cover resources here
    )


    private val bookTitles = listOf(
        "Sample Book 1",
        // Add more book titles here
    )

    private val bookDescriptions = listOf(
        "Description for Sample Book 1",
        // Add more book descriptions here
    )

    private var currentBookIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        initializeViews()
        // Set up gesture detector for swipe
        setupGestureDetector()
        // Set up click listeners
        setupClickListeners()
        // Load initial book
        loadCurrentBook()
    }

    private fun initializeViews() {
        // Find views
        bookImageView = findViewById<ImageView>(R.id.bookImage)
        heartButton = findViewById<ImageButton>(R.id.heartButton)
        recycleButton = findViewById<ImageButton>(R.id.recycleButton)
        titleText = findViewById<TextView>(R.id.bookTitle)
        homeButton = findViewById<ImageButton>(R.id.homeButton)
        profileButton = findViewById<ImageButton>(R.id.profileButton)
        bookDescription = findViewById<TextView>(R.id.bookDescription)

        // Set up button appearance
        heartButton.apply {
            setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_light))
            background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
        }

        recycleButton.apply {
            setColorFilter(ContextCompat.getColor(context, R.color.dark_green))  // Use your custom color
            background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
        }

        // Set up navigation button appearances
        homeButton.apply {
            setColorFilter(ContextCompat.getColor(context, android.R.color.black))
            background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
        }

        profileButton.apply {
            setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray))
            background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
        }

        // Set up touch listener for the image
        bookImageView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }



    private fun setupClickListeners() {
        heartButton.setOnClickListener {
            animateAndLike()
        }

        recycleButton.setOnClickListener {
            animateAndSkip()
        }

        // Add navigation click listeners
        homeButton.setOnClickListener {
            // Handle home button click
            homeButton.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
            profileButton.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
            // TODO: Implement home navigation
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        }

            profileButton.setOnClickListener {
                // Create and start the intent for FavoritesActivity
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)

                // Update button colors (although this might not be necessary since we're leaving this activity)
                profileButton.setColorFilter(ContextCompat.getColor(this, android.R.color.black))
                homeButton.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - (e1?.x ?: 0f)
                val diffY = e2.y - (e1?.y ?: 0f)

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Right swipe - Like
                            animateAndLike()
                        } else {
                            // Left swipe - Skip
                            animateAndSkip()
                        }
                        return true
                    }
                }
                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Handle single tap - could be used to show book details
                showBookDetails()
                return true
            }
        })
    }


    private fun animateAndLike() {
        val animation = TranslateAnimation(
            0f, bookImageView.width.toFloat(),
            0f, 0f
        ).apply {
            duration = 300
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    heartButton.isEnabled = false
                    recycleButton.isEnabled = false
                }
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    bookImageView.translationX = 0f
                    likeCurrentItem()
                    heartButton.isEnabled = true
                    recycleButton.isEnabled = true
                }
            })
        }
        bookImageView.startAnimation(animation)
    }

    private fun animateAndSkip() {
        val animation = TranslateAnimation(
            0f, -bookImageView.width.toFloat(),
            0f, 0f
        ).apply {
            duration = 300
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    heartButton.isEnabled = false
                    recycleButton.isEnabled = false
                }
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    bookImageView.translationX = 0f
                    skipCurrentItem()
                    heartButton.isEnabled = true
                    recycleButton.isEnabled = true
                }
            })
        }
        bookImageView.startAnimation(animation)
    }

    private fun loadCurrentBook() {
        if (currentBookIndex < bookCovers.size) {
            bookImageView.setImageResource(bookCovers[currentBookIndex])
            titleText.text = bookTitles.getOrNull(currentBookIndex) ?: "Unknown Title"
            bookDescription.text = bookDescriptions.getOrNull(currentBookIndex)
                ?: "No description available"
        } else {
            currentBookIndex = 0
            loadCurrentBook()
        }
    }

    private fun loadNextBook() {
        currentBookIndex = (currentBookIndex + 1) % bookCovers.size
        loadCurrentBook()
    }

    private fun likeCurrentItem() {
        // Animate heart button
        heartButton.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .withEndAction {
                heartButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
        // TODO: Add your like logic here (e.g., save to database, make API call)
        loadNextBook()
    }

    private fun skipCurrentItem() {
        // Animate skip button
        recycleButton.animate()
            .rotation(recycleButton.rotation + 360f)
            .setDuration(300)
            .start()
        // TODO: Add your skip logic here
        loadNextBook()
    }

    private fun showBookDetails() {
        // TODO: Implement book details view
        Toast.makeText(this, "Showing details for: ${bookTitles.getOrNull(currentBookIndex)}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}