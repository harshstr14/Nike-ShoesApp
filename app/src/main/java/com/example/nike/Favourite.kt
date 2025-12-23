package com.example.nike

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nike.databinding.ActivityFavouriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Favourite : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var favouriteList: ArrayList<CartItem>
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = false

        handleBottomNavPosition()

        setStatusBarIconsTheme(this)

        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid

        favouriteList = ArrayList()

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!).child("Favourite")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favouriteList.clear()
                for (shoeSnapShot in snapshot.children) {
                    val item = shoeSnapShot.getValue(CartItem::class.java)
                    item?.let {
                        favouriteList.add(it)
                    }
                }

                favouriteAdapter = FavouriteAdapter(favouriteList,userID,
                    onItemDeleted = { itemID ->
                        val index = favouriteList.indexOfFirst { it.id == itemID }
                        if (index != -1) {
                            favouriteList.removeAt(index)
                            favouriteAdapter.notifyItemRemoved(index)
                        }
                    })

                binding.recyclerViewFavourite.adapter = favouriteAdapter
                binding.recyclerViewFavourite.layoutManager = GridLayoutManager(this@Favourite,2)

                favouriteAdapter.setOnItemClickListener(object : FavouriteAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@Favourite, Details::class.java)
                        intent.putExtra("category","Favourite")
                        intent.putExtra("userID",userID)
                        intent.putExtra("shoeID",favouriteList[position].id)
                        intent.putExtra("shoeName",favouriteList[position].name)
                        intent.putExtra("shoeType",favouriteList[position].type)
                        intent.putExtra("shoeImage",favouriteList[position].imageURL)
                        intent.putExtra("shoePrice",favouriteList[position].price)
                        intent.putExtra("shoeDescription",favouriteList[position].description)
                        intent.putExtra("shoeProductDetails",favouriteList[position].productDetails)
                        startActivity(intent)
                    }
                })
                favouriteAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Favourite", "Database error: ${error.message}")
                Toast.makeText(this@Favourite, "Failed to load shoes", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun Int.dpToPx(view: View): Int =
        (this * view.resources.displayMetrics.density).toInt()
    private fun handleBottomNavPosition() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->

            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // Typical values:
            // Gesture: 16–24dp
            // 3-button: 48–80dp

            val threshold = 40.dpToPx(binding.root)

            binding.main.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (navBarHeight > threshold) {
                    navBarHeight   // 3-button → move up
                } else {
                    0              // Gesture → stay at bottom
                }
            }

            insets
        }
    }
    private fun setStatusBarIconsTheme(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView
        val insetsController = WindowInsetsControllerCompat(window, decorView)

        // Detect current theme
        val isDarkTheme =
            (activity.resources.configuration.uiMode
                    and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Set icon color automatically
        if (isDarkTheme) {
            // Light icons for dark theme
            insetsController.isAppearanceLightStatusBars = true
        } else {
            // Dark icons for light theme
            insetsController.isAppearanceLightStatusBars = true
        }
    }
}