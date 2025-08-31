package com.example.nike

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter (private val categories: List<String>,private val onItemSelected: (Int) -> Unit):
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var selectedPosition = 0
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.name?.text = categories[position]
        holder.container?.isSelected = position == selectedPosition
        holder.name?.setTextColor(if (position == selectedPosition) Color.WHITE else Color.GRAY)

        if (position == selectedPosition) {
            val animRes = if (position > lastPosition) {
                R.anim.slide_in_left_to_right
            } else {
                R.anim.slide_in_right_to_left
            }
            val anim = AnimationUtils.loadAnimation(holder.itemView.context, animRes)
            holder.itemView.startAnimation(anim)
        }

        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            lastPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previous)
                notifyItemChanged(position)
                onItemSelected(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
    inner class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView? = view.findViewById(R.id.CategoryName)
        val container: View? = view.findViewById(R.id.CategoryItem)
    }
}