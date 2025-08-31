package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val orderList: List<OrderItem>) : RecyclerView.Adapter<OrdersAdapter.OrderAdapterViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order,parent,false)
        return OrderAdapterViewHolder(view,myListener)
    }

    override fun onBindViewHolder(holder: OrderAdapterViewHolder, position: Int) {
        holder.orderID?.text = orderList[position].orderID
        "${orderList[position].date}".also { holder.dateText?.text = it }
        holder.timeText?.text = orderList[position].time
        holder.amountText?.text = orderList[position].amount

        val productList = orderList[position].products
        val items = productList?.map { it.quantity }
        var totalItems = 0
        if (items != null) {
            for (item in items) {
                totalItems += item
            }
        }
        "$totalItems Items".also { holder.totalItemsText?.text = it }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderAdapterViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val orderID: TextView? = view.findViewById(R.id.orderIDTextView)
        val dateText: TextView? = view.findViewById(R.id.dayTextView)
        val timeText: TextView? = view.findViewById(R.id.timeTextView)
        val amountText: TextView? = view.findViewById(R.id.amountTextView)
        val totalItemsText: TextView ?= view.findViewById(R.id.textView2)

        init {
            view.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }
}