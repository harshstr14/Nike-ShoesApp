package com.example.nike

data class Products (
    val price: Double ?= 0.0,
    var quantity: Int = 1,
    val shoeID: Int ?= 0,
    val shoeImage: String ?= "",
    val shoeName: String ?= "",
    val size: String ?= ""
)
