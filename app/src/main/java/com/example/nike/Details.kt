package com.example.nike

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nike.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class Details : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        enableEdgeToEdgeWithInsets(binding.root,binding.main)
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

        binding.cartBackground.setOnClickListener {
            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        binding.cartImage.setOnClickListener {
            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        val sizeCategoryList = listOf(
            "UK 6","UK 7","UK 8","UK 9"
        )

        var sizePosition = 1
        val sizeAdapter = SizeCategoryAdapter(sizeCategoryList){ position ->
            sizePosition = position
            Log.d("SizeClick", "Selected size at position: $sizePosition")
        }
        binding.sizeCategory.adapter = sizeAdapter
        binding.sizeCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        val category = intent.getStringExtra("category")
        val shoeID = intent.getIntExtra("shoeID",0)
        val image = intent.getStringExtra("shoeImage")

        val shoeImageList = mutableListOf<String>()
        when (category) {
            "Favourite" -> {
                Log.d("FIREBASE", "Category: $category")
                val userID = intent.getStringExtra("userID")
                database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!).child(category)
            }
            "MyCart" -> {
                Log.d("FIREBASE", "Category: $category")
                val userID = intent.getStringExtra("userID")
                database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!).child(category)
            }
            else -> {
                Log.d("FIREBASE", "Category: $category")
                database = FirebaseDatabase.getInstance().getReference().child(category!!)
            }
        }
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (shoeSnapshot in snapshot.children) {
                    val id = shoeSnapshot.child("id").getValue(Int::class.java) ?: 0
                    if (id == shoeID) {
                        val imageSnapshot = shoeSnapshot.child("shoeImages")
                        for (image in imageSnapshot.children) {
                            image.getValue(String::class.java)?.let {
                                shoeImageList.add(it)
                            }
                        }
                        val shoeImageAdapter = ShoeImageAdapter(shoeImageList) { position ->
                            if (position == 0) {
                                binding.shoeImage.visibility = View.VISIBLE
                                binding.shoeImageView.visibility = View.GONE
                            } else {
                                binding.shoeImage.visibility = View.GONE
                                binding.shoeImageView.visibility = View.VISIBLE
                                Picasso.get().load(shoeImageList[position]).into(binding.shoeImageView)
                            }
                        }
                        binding.shoeImageRecyclerView.adapter = shoeImageAdapter
                        binding.shoeImageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                        Log.d("FIREBASE", "Loaded images: $shoeImageList")
                        break
                    }
                }
            } else {
                Log.d("FIREBASE", "No shoe found for ID = $shoeID")
            }
        }

        val name = intent.getStringExtra("shoeName")
        val type = intent.getStringExtra("shoeType")
        val price = intent.getDoubleExtra("shoePrice",0.0)
        val description = intent.getStringExtra("shoeDescription")
        val productDetails = intent.getStringExtra("shoeProductDetails")
        val quantity = 1

        Picasso.get().load(image).into(binding.shoeImage)
        binding.shoeNameTittle.text = name
        binding.shoeNameText.text = name
        binding.shoeTypeText.text = type
        binding.shoeDescription.text = description
        binding.productDetail.text = productDetails
        "$ $price".also { binding.priceText.text = it }

        binding.addToCartBtn.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val shoeSize = sizeCategoryList[sizePosition]
            val userID = auth.currentUser?.uid

            if (userID != null) {
                database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("MyCart")
                    .child(shoeID.toString())
                database.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val shoes = CartItem(
                            description = description,
                            id = shoeID,
                            imageURL = image,
                            name = name,
                            type = type,
                            price = price,
                            shoeImages = shoeImageList,
                            productDetails = productDetails,
                            quantity = quantity,
                            shoeSize = shoeSize
                        )
                        database.setValue(shoes).addOnSuccessListener {
                            Log.d("Cart", "Shoe added to cart: $shoeID")
                            Toast.makeText(this, "Shoe added to cart", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.e("Cart", "Failed to add shoe: ${it.message}")
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("Cart", "Shoe already in cart: $shoeID")
                        Toast.makeText(this, "Shoe already in cart", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e("Cart", "Error fetching cart data: ${it.message}")
                    Toast.makeText(this, "Unable to access cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Auth", "UID is null after successful registration")
                Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        binding.addFavourite.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val userID = auth.currentUser?.uid
            val shoeSize = sizeCategoryList[sizePosition]

            if (userID != null) {
                database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Favourite")
                    .child(shoeID.toString())
                database.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val shoes = CartItem(
                            description = description,
                            id = shoeID,
                            imageURL = image,
                            name = name,
                            type = type,
                            price = price,
                            shoeImages = shoeImageList,
                            productDetails = productDetails,
                            quantity = quantity,
                            shoeSize = shoeSize
                        )
                        database.setValue(shoes).addOnSuccessListener {
                            Log.d("Favourite", "Shoe added to favourite: $shoeID")
                            Toast.makeText(this, "Shoe added to Favourite", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.e("Favourite", "Failed to add shoe: ${it.message}")
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("Favourite", "Shoe already in favourite: $shoeID")
                        Toast.makeText(this, "Shoe already in Favourite", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e("Favourite", "Error fetching Favourite data: ${it.message}")
                    Toast.makeText(this, "Unable to access Favourite List", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun enableEdgeToEdgeWithInsets(rootView: View, LayoutView: View) {
        val activity = rootView.context as ComponentActivity
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            LayoutView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
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