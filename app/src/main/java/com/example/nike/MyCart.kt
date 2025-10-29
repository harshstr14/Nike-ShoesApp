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
import com.example.nike.databinding.ActivityMyCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class MyCart : AppCompatActivity() {
    private lateinit var binding: ActivityMyCartBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var  myCartList: ArrayList<CartItem>
    private lateinit var myCartAdapter: MyCartAdapter
    private var subTotal = 0.0
    private var shoppingFee = 0.0
    private var totalCost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyCartBinding.inflate(layoutInflater)
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
        if (userID != null) {
            database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("MyCart")
            database.addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    myCartList = ArrayList()
                    for (shoeSnapshot in snapshot.children) {
                        val item = shoeSnapshot.getValue(CartItem::class.java)
                        item?.let {
                            myCartList.add(it)
                        }
                    }

                    myCartAdapter = MyCartAdapter(myCartList,userID,
                        onCartUpdated = {
                            updateTotalCost()
                        },
                        onItemDeleted = { itemId ->
                            val index = myCartList.indexOfFirst { it.id == itemId }
                            if (index != -1) {
                                myCartList.removeAt(index)
                                myCartAdapter.notifyItemRemoved(index)
                                updateTotalCost()
                            }
                        }
                    )

                    binding.recyclerView2.adapter = myCartAdapter
                    binding.recyclerView2.layoutManager = LinearLayoutManager(this@MyCart, LinearLayoutManager.VERTICAL,false)
                    updateTotalCost()

                    myCartAdapter.setOnClickListener(object : MyCartAdapter.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@MyCart, Details::class.java)
                            intent.putExtra("category","MyCart")
                            intent.putExtra("userID",userID)
                            intent.putExtra("shoeID",myCartList[position].id)
                            intent.putExtra("shoeName",myCartList[position].name)
                            intent.putExtra("shoeType",myCartList[position].type)
                            intent.putExtra("shoeImage",myCartList[position].imageURL)
                            intent.putExtra("shoePrice",myCartList[position].price)
                            intent.putExtra("shoeDescription",myCartList[position].description)
                            intent.putExtra("shoeProductDetails",myCartList[position].productDetails)
                            startActivity(intent)
                        }
                    })
                    myCartAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyCart", "Database error: ${error.message}")
                    Toast.makeText(this@MyCart, "Failed to load shoes", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.e("Auth", "UID is null after successful registration")
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
        }

        binding.checkOutBtn.setOnClickListener {
            if (myCartList.isNotEmpty()) {
                val intent = Intent(this, CheckOut::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtra("subTotal",subTotal)
                intent.putExtra("shoppingFee",shoppingFee)
                intent.putExtra("totalCost",totalCost)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cart is Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateTotalCost() {
        subTotal = 0.0
        for (item in myCartList) {
            subTotal += item.quantity * (item.price ?: 0.0)
        }
        shoppingFee = 40.90
        totalCost = subTotal + shoppingFee
        binding.subTotalText.text = String.format(Locale.US,"$ %.2f",subTotal)
        binding.shoppingFeeText.text = String.format(Locale.US,"$ %.2f",shoppingFee)
        binding.totalCostText.text = String.format(Locale.US,"$ %.2f",totalCost)
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