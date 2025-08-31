package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class FavouriteAdapter(private val favouriteList: List<CartItem>, userID: String, private val onItemDeleted: (Int) -> Unit): RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Favourite")
    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite,parent,false)
        return FavouriteViewHolder(view,myListener)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        Picasso.get().load(favouriteList[position].imageURL).into(holder.image)
        holder.name?.text = favouriteList[position].name
        holder.type?.text = favouriteList[position].type
        val price= favouriteList[position].price.toString()
        "$ $price".also { holder.price?.text = it }

        holder.removeFav?.setOnClickListener {
            val itemID = favouriteList[position].id
            database.child(itemID.toString()).removeValue().addOnSuccessListener {
                onItemDeleted(itemID!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    inner class FavouriteViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView? = view.findViewById(R.id.shoesImage)

        val name: TextView? = view.findViewById(R.id.shoesName)

        val type: TextView? = view.findViewById(R.id.shoesType)

        val price: TextView? = view.findViewById(R.id.shoesPrice)
        val removeFav: ImageView? = view.findViewById(R.id.remove_favourite)

        init {
            view.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}