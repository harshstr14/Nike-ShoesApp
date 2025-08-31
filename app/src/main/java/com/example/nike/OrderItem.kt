package com.example.nike

data class OrderItem (
    val address: String ?= "",
    val amount: String ?= "",
    val date: String ?= "",
    val orderID: String ?= "",
    var products: MutableList<Products>? = null,
    val time: String ?= "",
    val transactionID: String ?= ""
)
