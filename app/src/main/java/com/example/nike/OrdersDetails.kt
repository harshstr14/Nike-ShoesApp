package com.example.nike

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
}