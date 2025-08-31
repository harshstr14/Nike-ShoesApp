package com.example.nike

data class CartItem(
    val description: String ?= "",
    val id: Int ?= 0,
    val imageURL: String ?= "",
    val name: String ?= "",
    val price: Double ?= 0.0,
    val shoeImages: List<String> ?= null,
    val productDetails: String ?= "",
    val type: String ?= "",
    var quantity: Int = 1,
    val shoeSize: String ?= ""
)
