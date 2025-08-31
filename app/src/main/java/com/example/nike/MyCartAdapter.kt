package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.Locale

class MyCartAdapter(private val myCartList: List<CartItem>, userID: String, private val onCartUpdated: () -> Unit, private val onItemDeleted: (Int) -> Unit)
    : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    private val database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("MyCart")

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart,parent,false)
        return MyCartViewHolder(view,myListener)
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val item = myCartList[position]
        Picasso.get().load(item.imageURL).into(holder.image)
        //holder.image?.setImageResource(myCartList[position].image)
        holder.name?.text = item.name
        holder.itemQuantityText?.text = item.quantity.toString()
        holder.shoeSizeText?.text = item.shoeSize
        val totalPrice = (item.price ?: 0.0) * item.quantity
        holder.price?.text = String.format(Locale.US, "$ %.2f", totalPrice)

        holder.minusBtn?.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.itemQuantityText?.text = item.quantity.toString()
                val updateTotal = item.quantity * (item.price ?: 0.0)
                holder.price?.text = String.format(Locale.US,"$ %.2f", updateTotal)
                database.child(item.id.toString()).child("quantity").setValue(item.quantity)
                onCartUpdated.invoke()
            }
        }
        holder.minusBtnBackGround?.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.itemQuantityText?.text = item.quantity.toString()
                val updateTotal = item.quantity * (item.price ?: 0.0)
                holder.price?.text = String.format(Locale.US,"$ %.2f", updateTotal)
                database.child(item.id.toString()).child("quantity").setValue(item.quantity)
                onCartUpdated.invoke()
            }
        }

        holder.plusBtn?.setOnClickListener {
            if (item.quantity < 10) {
                item.quantity++
                holder.itemQuantityText?.text = item.quantity.toString()
                val updateTotal = item.quantity * (item.price ?: 0.0)
                holder.price?.text = String.format(Locale.US,"$ %.2f", updateTotal)
                database.child(item.id.toString()).child("quantity").setValue(item.quantity)
                onCartUpdated.invoke()
            }
        }
        holder.plusBtnBackground?.setOnClickListener {
            if (item.quantity < 10) {
                item.quantity++
                holder.itemQuantityText?.text = item.quantity.toString()
                val updateTotal = item.quantity * (item.price ?: 0.0)
                holder.price?.text = String.format(Locale.US,"$ %.2f", updateTotal)
                database.child(item.id.toString()).child("quantity").setValue(item.quantity)
                onCartUpdated.invoke()
            }
        }

        holder.deleteButton?.setOnClickListener {
            val itemID = item.id
            if (itemID != null) {
                database.child(itemID.toString()).removeValue().addOnSuccessListener {
                    onItemDeleted(itemID)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return myCartList.size
    }
    class MyCartViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView ?= view.findViewById(R.id.imageView)

        val name: TextView ?= view.findViewById(R.id.textView13)

        val price: TextView ?= view.findViewById(R.id.textView14)
        var itemQuantityText: TextView ?= view.findViewById(R.id.itemQuantities)
        var shoeSizeText: TextView ?= view.findViewById(R.id.shoeSizeText)
        val minusBtn: ImageView ?= view.findViewById(R.id.minusBtn)
        val plusBtn: ImageView ?= view.findViewById(R.id.addBtn)
        val minusBtnBackGround: ImageView ?= view.findViewById(R.id.appCompatImageView3)
        val plusBtnBackground: ImageView ?= view.findViewById(R.id.appCompatImageView4)
        val deleteButton: ImageButton ?= view.findViewById(R.id.removeBtn)

        init {
            view.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}