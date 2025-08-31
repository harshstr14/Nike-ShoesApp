package com.example.nike

data class Shoe(
    val description: String = "",
    val id: Int = 0,
    val imageURL: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val productDetails: List<String> = listOf(),
    val type: String = ""
)