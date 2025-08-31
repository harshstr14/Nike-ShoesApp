package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SizeCategoryAdapter(private val sizeList: List<String>,private val onItemSelected: (Int) -> Unit)
    : RecyclerView.Adapter<SizeCategoryAdapter.SizeCategoryViewHolder>() {
    private var selectedPosition = 1
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): SizeCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sizecategory,parent,false)
        return SizeCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SizeCategoryViewHolder, position: Int) {
        holder.size?.text = sizeList[position]
        holder.container?.isSelected = position == selectedPosition

        if (position == selectedPosition) {
            val animRes = if (position > lastPosition) {
                R.anim.slide_in_top_to_bottom
            } else {
                R.anim.slide_in_bottom_to_top
            }
            val anim = AnimationUtils.loadAnimation(holder.itemView.context,animRes)
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
        return sizeList.size
    }
    inner class SizeCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val size: TextView? = view.findViewById(R.id.sizeCategoryName)
        val container: View? = view.findViewById(R.id.sizeCategoryItem)

        init {
            view.setOnClickListener {
                onItemSelected(bindingAdapterPosition)
            }
        }
    }
}