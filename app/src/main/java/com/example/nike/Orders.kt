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
import com.example.nike.databinding.ActivityOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Orders : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var ordersList = mutableListOf<OrderItem>()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Transactions Details")
        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ordersList.clear()

                for (orderSnapshot in snapshot.children) {
                    val address = orderSnapshot.child("address").getValue(String::class.java) ?: ""
                    val amount = orderSnapshot.child("amount").getValue(String::class.java) ?: ""
                    val date = orderSnapshot.child("date").getValue(String::class.java) ?: ""
                    val orderID = orderSnapshot.child("orderID").getValue(String::class.java) ?: ""
                    val time = orderSnapshot.child("time").getValue(String::class.java) ?: ""
                    val transactionID = orderSnapshot.child("transactionID").getValue(String::class.java) ?: ""

                    val productsMap = orderSnapshot.child("products")
                    val productsList = mutableListOf<Products>()
                    for (productSnapshot in productsMap.children) {
                        productSnapshot.getValue(Products::class.java)?.let { productsList.add(it) }
                    }

                    val order = OrderItem(
                        address = address,
                        amount = amount,
                        date = date,
                        orderID = orderID,
                        products = productsList,
                        time = time,
                        transactionID = transactionID
                    )
                    ordersList.add(order)
                }

                ordersAdapter = OrdersAdapter(ordersList)
                binding.orderRecyclerView.adapter = ordersAdapter
                binding.orderRecyclerView.layoutManager = LinearLayoutManager(this@Orders,LinearLayoutManager.VERTICAL,false)
                ordersAdapter.setOnItemClickListener(object : OrdersAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val intent = Intent(this@Orders, OrdersDetails::class.java)
                        intent.putExtra("address",ordersList[position].address)
                        intent.putExtra("transactionID",ordersList[position].transactionID)
                        intent.putExtra("orderID",ordersList[position].orderID)
                        intent.putExtra("date",ordersList[position].date)
                        intent.putExtra("time",ordersList[position].time)
                        startActivity(intent)
                    }
                })
                ordersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Orders", "Database error: ${error.message}")
                Toast.makeText(this@Orders, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        })
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