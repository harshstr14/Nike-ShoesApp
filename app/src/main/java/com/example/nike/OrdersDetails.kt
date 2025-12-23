package com.example.nike

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.example.nike.databinding.ActivityOrdersDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class OrdersDetails : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersDetailsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var productsList = mutableListOf<Products>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrdersDetailsBinding.inflate(layoutInflater)
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

        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, Orders::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, Orders::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        val address = intent.getStringExtra("address")
        val transactionID = intent.getStringExtra("transactionID")
        val orderID = intent.getStringExtra("orderID")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")

        binding.transactionIDTextView.text = transactionID
        binding.orderIDTextView.text = orderID
        binding.dayTextView.text = date
        binding.timeTextView.text = time
        binding.addressTextView.text = address

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Transactions Details").child(transactionID!!).child("products")
        database.get().addOnSuccessListener { snapshot ->
            for (productSnapshot in snapshot.children) {
                productSnapshot.getValue(Products::class.java)?.let { productsList.add(it) }
            }
            val shoeNameList = productsList.joinToString("\n\n") {
                "${it.shoeName}  x  ${it.quantity}"
            }
            val priceList = productsList.joinToString("\n\n") {
                "$ ${it.price?.times(it.quantity)}"
            }

            binding.amountTextView.text = priceList
            binding.productTextView.text = shoeNameList
            updateTotal()
        }
    }
    private fun updateTotal() {
        var subTotal = 0.0
        for (item in productsList) {
            subTotal += item.quantity * (item.price ?: 0.0)
        }
        val shoppingFee = 40.90
        val totalCost = subTotal + shoppingFee
        binding.subtotalTextView.text = String.format(Locale.US,"$ %.2f",subTotal)
        binding.shoppingFeeTextView.text = String.format(Locale.US,"$ %.2f",shoppingFee)
        binding.totalCostTextView.text = String.format(Locale.US,"$ %.2f",totalCost)
        binding.paidTextView.text = String.format(Locale.US,"$ %.2f",totalCost)
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