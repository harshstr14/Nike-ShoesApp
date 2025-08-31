package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SearchAdapter(private var searchList: List<Shoe>): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun omItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search,parent,false)
        return SearchViewHolder(view,myListener)
    }
    fun filterList(searchList: List<Shoe>) {
        this.searchList = searchList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        Picasso.get().load(searchList[position].imageURL).into(holder.shoeImage)
        holder.shoeName?.text = searchList[position].name
        holder.shoeType?.text = searchList[position].type
        "$ ${searchList[position].price}".also { holder.shoePrice?.text = it }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    class SearchViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val shoeImage: AppCompatImageView ?= view.findViewById(R.id.shoeImageView)
        val shoeName: TextView ?= view.findViewById(R.id.shoeNametextView)
        val shoeType: TextView ?= view.findViewById(R.id.shoeTypetextView)
        val shoePrice: TextView ?= view.findViewById(R.id.amountTextView)

        init {
            view.setOnClickListener {
                listener.omItemClick(bindingAdapterPosition)
            }
        }
    }
}