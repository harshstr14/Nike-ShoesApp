package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nike.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Search : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var shoesList = mutableListOf<Shoe>()
    private lateinit var filteredList: ArrayList<Shoe>
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

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
        binding.cancleTextView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        val category = intent.getStringExtra("category")
        searchAdapter = SearchAdapter(shoesList)
        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        loadShoesData(category!!)

        searchAdapter.setOnItemClickListener(object : SearchAdapter.OnItemClickListener {
            override fun omItemClick(position: Int) {
                if (filteredList.isNotEmpty()) {
                    val intent = Intent(this@Search, Details::class.java)
                    intent.putExtra("category",category)
                    intent.putExtra("shoeID",filteredList[position].id)
                    intent.putExtra("shoeName",filteredList[position].name)
                    intent.putExtra("shoeType",filteredList[position].type)
                    intent.putExtra("shoeImage",filteredList[position].imageURL)
                    intent.putExtra("shoePrice",filteredList[position].price)
                    intent.putExtra("shoeDescription",filteredList[position].description)

                    val productDetails = StringBuilder()
                    for (item in filteredList[position].productDetails) {
                        productDetails.append("• ").append(item).append("\n")
                    }

                    intent.putExtra("shoeProductDetails",productDetails.toString())
                    startActivity(intent)
                }
            }
        })

        binding.searchView.setOnQueryTextListener(object : CustomSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText,shoesList)
                return true
            }
        })
    }
    private fun loadShoesData(category: String) {
        shoesList.clear()
        searchAdapter.notifyDataSetChanged()

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

                filteredList = ArrayList(shoesList)

                searchAdapter.notifyDataSetChanged()
                Log.d("FirebaseShoes", "Shoes loaded: ${shoesList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseShoes", "Database error: ${error.message}")
                Toast.makeText(this@Search, "Failed to load shoes", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun filterList(newText: String?,shoeList: List<Shoe>) {
        if (newText != null) {
            filteredList = ArrayList()
            for (i in shoeList) {
                if (i.name.lowercase().contains(newText.lowercase())) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this,"Shoe Not Found", Toast.LENGTH_SHORT).show()
            } else {
                searchAdapter.filterList(filteredList)
            }
        }
    }
}