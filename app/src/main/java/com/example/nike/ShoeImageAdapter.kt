package com.example.nike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ShoeImageAdapter(private val imageList: List<String>, private val onItemSelected: (Int) -> Unit)
    : RecyclerView.Adapter<ShoeImageAdapter.ShoeImageViewHolder>() {

    private var selectedPosition = 0
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shoe_image,parent,false)
        return ShoeImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoeImageViewHolder, position: Int) {
        Picasso.get().load(imageList[position]).into(holder.image)
        holder.container?.isSelected = position == selectedPosition

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
        return imageList.size
    }

    inner class ShoeImageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: AppCompatImageView? = view.findViewById(R.id.shoeImageView)
        val container: View? = view.findViewById(R.id.shoeImageItem)
    }
}