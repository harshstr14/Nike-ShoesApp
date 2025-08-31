package com.example.nike

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ShoesAdapter(val context: Context,val shoesList: List<Shoe>): RecyclerView.Adapter<ShoesAdapter.ShoesViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shoes,parent,false)
        return ShoesViewHolder(view,myListener)
    }

    override fun onBindViewHolder(holder: ShoesViewHolder, position: Int) {
        Picasso.get().load(shoesList[position].imageURL).into(holder.image)
        //holder.image?.setImageResource(shoesList[position].image)
        holder.name?.text = shoesList[position].name
        holder.type?.text = shoesList[position].type
        val price= shoesList[position].price.toString()
        "$ $price".also { holder.price?.text = it }
    }

    override fun getItemCount(): Int {
        return shoesList.size
    }

    class ShoesViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val image: ImageView? = view.findViewById(R.id.shoesImage)

        val name: TextView? = view.findViewById(R.id.shoesName)

        val type: TextView? = view.findViewById(R.id.shoesType)

        val price: TextView? = view.findViewById(R.id.shoesPrice)

        init {
            view.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}