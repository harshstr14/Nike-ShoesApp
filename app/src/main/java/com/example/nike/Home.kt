package com.example.nike

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.nike.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var shoesAdapter: ShoesAdapter
    private var shoesList = mutableListOf<Shoe>()
    private var bannerList = mutableListOf<Shoe>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var navView: NavigationView
    private var googleSignInManager: GoogleSignInManager ?= null
    private var bannerOffset = 0
    private var isBannerHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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

        googleSignInManager = GoogleSignInManager.getInstance(this)
        googleSignInManager?.setupGoogleSignInOptions()

        val bannerImageURl = mutableListOf<String>()

        val bannerRef = FirebaseDatabase.getInstance().getReference().child("Banner")
        bannerRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (shoeSnapshot in snapshot.children) {
                    val id = shoeSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val name = shoeSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = shoeSnapshot.child("description").getValue(String::class.java) ?: ""
                    val imageURL = shoeSnapshot.child("imageURL").getValue(String::class.java) ?: ""
                    val price = shoeSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                    val type = shoeSnapshot.child("type").getValue(String::class.java) ?: ""
                    val bannerImage = shoeSnapshot.child("bannerURL").getValue(String::class.java) ?: ""
                    bannerImageURl.add(bannerImage)

                    val productDetails = mutableListOf<String>()
                    val detailsSnapshot = shoeSnapshot.child("productDetails")
                    for (detail in detailsSnapshot.children) {
                        detail.getValue(String::class.java)?.let {
                            productDetails.add(it)
                        }
                    }

                    val shoe = Shoe(
                        id = id,
                        name = name,
                        description = description,
                        imageURL = imageURL,
                        price = price,
                        type = type,
                        productDetails = productDetails
                    )
                    bannerList.add(shoe)
                }

                val imageList = ArrayList<SlideModel>()
                for (image in bannerImageURl.indices) {
                    imageList.add(SlideModel(bannerImageURl[image], ScaleTypes.CENTER_CROP))
                }

                val imageSlider = binding.imageSlider
                imageSlider.setImageList(imageList)
                imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

                imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun doubleClick(position: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onItemSelected(position: Int) {
                        val intent = Intent(this@Home, Details::class.java)
                        intent.putExtra("category","Banner")
                        intent.putExtra("shoeID",bannerList[position].id)
                        intent.putExtra("shoeName",bannerList[position].name)
                        intent.putExtra("shoeType",bannerList[position].type)
                        intent.putExtra("shoeImage",bannerList[position].imageURL)
                        intent.putExtra("shoePrice",bannerList[position].price)
                        intent.putExtra("shoeDescription",bannerList[position].description)

                        val productDetails = StringBuilder()
                        for (item in bannerList[position].productDetails) {
                            productDetails.append("• ").append(item).append("\n")
                        }

                        intent.putExtra("shoeProductDetails",productDetails.toString())
                        startActivity(intent)
                    }
                })
            }
        }

        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        toolbar = binding.toolBar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )

        toggle.setHomeAsUpIndicator(R.drawable.hamburger_icon)
        toggle.isDrawerIndicatorEnabled = false

        toolbar.setNavigationOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                if (!supportFragmentManager.popBackStackImmediate()) {
                    finish()
                }
            }
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

        navView = binding.navView
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_home -> {
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_cart -> {
                    val intent = Intent(this, MyCart::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_favorite -> {
                    val intent = Intent(this, Favourite::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_orders -> {
                    val intent = Intent(this, Orders::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_notification -> {
                    val intent = Intent(this, Notifications::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
                R.id.nav_signOut -> {
                    val pref = getSharedPreferences("Pref_Name",MODE_PRIVATE)
                    pref.edit {putBoolean("isLoggedIn",false)}

                    googleSignInManager!!.signOut()

                    val intent = Intent(this, SignIn::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid
        val headerView = navView.getHeaderView(0)
        val profileImageView = headerView.findViewById<CircleImageView>(R.id.profile_image)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
        database.get().addOnSuccessListener {
            val imageUrl = it.child("Profile ImageUrl").value.toString()
            val name = it.child("name").value.toString()
            Picasso.get().load(imageUrl).into(profileImageView)
            nameTextView.text = name
        }

        selectNavItem(R.id.bottom_nav_home)
        binding.bottomNavHome.setOnClickListener {
            selectNavItem(R.id.bottom_nav_home)
        }
        binding.bottomNavFav.setOnClickListener {
            selectNavItem(R.id.bottom_nav_fav)
            val intent = Intent(this, Favourite::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        binding.bottomNavCart.setOnClickListener {
            selectNavItem(R.id.bottom_nav_fav)
            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        binding.bottomNavNotification.setOnClickListener {
            selectNavItem(R.id.bottom_nav_notification)
            val intent = Intent(this, Notifications::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
        binding.bottomNavUser.setOnClickListener {
            selectNavItem(R.id.bottom_nav_user)
            val intent = Intent(this, Profile::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        val categoryList = listOf(
            "All","Air Jordan 1","Air Force 1","Dunk","Blazer","V2K"
        )
        var category = categoryList[0]
        val adapter = CategoryAdapter(categoryList){ position ->
            category = categoryList[position]
            loadShoesData(category)
            Log.d("Category","Category : $category")
        }
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.categoryRecyclerView.adapter = adapter

        binding.searchView.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            intent.putExtra("category",category)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "Firebase initialized successfully")
        } ?: run {
            Log.e("FirebaseInit", "Firebase initialization failed")
        }

        shoesAdapter = ShoesAdapter(this,shoesList)
        binding.recyclerView.adapter = shoesAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.isNestedScrollingEnabled = false
        loadShoesData(category)

        shoesAdapter.setOnItemClickListener(object : ShoesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@Home, Details::class.java)
                intent.putExtra("category",category)
                intent.putExtra("shoeID",shoesList[position].id)
                intent.putExtra("shoeName",shoesList[position].name)
                intent.putExtra("shoeType",shoesList[position].type)
                intent.putExtra("shoeImage",shoesList[position].imageURL)
                intent.putExtra("shoePrice",shoesList[position].price)
                intent.putExtra("shoeDescription",shoesList[position].description)

                val productDetails = StringBuilder()
                for (item in shoesList[position].productDetails) {
                    productDetails.append("• ").append(item).append("\n")
                }

                intent.putExtra("shoeProductDetails",productDetails.toString())
                startActivity(intent)
            }
        })

        binding.imageSlider.post {
            bannerOffset = binding.imageSlider.height
        }

        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                if (scrollY > bannerOffset / 2 && !isBannerHidden) {
                    hideBanner()
                    isBannerHidden = true
                }
                else if (scrollY < bannerOffset / 2 && isBannerHidden) {
                    showBanner()
                    isBannerHidden = false
                }
            }
        )
    }
    private fun hideBanner() {
        val offset = bannerOffset.toFloat()

        // Banner slides out
        binding.imageSlider.animate()
            .translationY(-offset)
            .alpha(0f)
            .setDuration(220)
            .start()

//        // Category moves ONLY to the top (not beyond)
//        binding.categoryRecyclerView.animate()
//            .translationY(-offset)
//            .setDuration(220)
//            .start()
    }
    private fun showBanner() {
        binding.imageSlider.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(220)
            .start()

//        binding.categoryRecyclerView.animate()
//            .translationY(0f)
//            .setDuration(220)
//            .start()
    }
    private fun loadShoesData(category: String) {
        shoesList.clear()
        shoesAdapter.notifyDataSetChanged()

        database = FirebaseDatabase.getInstance().getReference()
        database.child(category).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseShoes", "Exists: ${snapshot.exists()}")
                Log.d("FirebaseShoes", "Children: ${snapshot.childrenCount}")

                for (shoeSnapshot in snapshot.children) {
                    Log.d("FirebaseShoes", "Item key: ${shoeSnapshot.key}")
                    Log.d("FirebaseShoes", "Item value: ${shoeSnapshot.value}")

                    val id = shoeSnapshot.child("id").getValue(Int::class.java) ?: 0
                    val name = shoeSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = shoeSnapshot.child("description").getValue(String::class.java) ?: ""
                    val imageURL = shoeSnapshot.child("imageURL").getValue(String::class.java) ?: ""
                    val price = shoeSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                    val type = shoeSnapshot.child("type").getValue(String::class.java) ?: ""

                    val productDetails = mutableListOf<String>()
                    val detailsSnapshot = shoeSnapshot.child("productDetails")
                    for (detail in detailsSnapshot.children) {
                        detail.getValue(String::class.java)?.let {
                            productDetails.add(it)
                        }
                    }

                    val shoe = Shoe(
                        id = id,
                        name = name,
                        description = description,
                        imageURL = imageURL,
                        price = price,
                        type = type,
                        productDetails = productDetails
                    )
                    shoesList.add(shoe)
                }

                shoesAdapter.notifyDataSetChanged()
                Log.d("FirebaseShoes", "Shoes loaded: ${shoesList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseShoes", "Database error: ${error.message}")
                Toast.makeText(this@Home, "Failed to load shoes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        selectNavItem(R.id.bottom_nav_home)
    }
    private fun selectNavItem(selectedId: Int) {
        val animation = AnimationUtils.loadAnimation(this@Home,R.anim.bounce_up)
        val navItem = listOf(R.id.bottom_nav_home,R.id.bottom_nav_fav,R.id.bottom_nav_cart,
            R.id.bottom_nav_notification,R.id.bottom_nav_user)
        navItem.forEach { id ->
            val item = findViewById<ImageView>(id)
            item.isSelected = id == selectedId
            if (id == selectedId) {
                item.startAnimation(animation)
            }
        }
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

            binding.drawerLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
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
            insetsController.isAppearanceLightStatusBars = false
        } else {
            // Dark icons for light theme
            insetsController.isAppearanceLightStatusBars = true
        }
    }
}