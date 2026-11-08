package com.example.restaurantguide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantguide.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private val onClick: (RestaurantWithRating) -> Unit
) : ListAdapter<RestaurantWithRating, RestaurantAdapter.VH>(DIFF) {

    class VH(val b: ItemRestaurantBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvName.text = item.restaurant.name
        holder.b.tvRating.text = ratingLabel(item.avgRating, item.reviewCount)
        holder.b.tvTags.text = tagsLabel(item.restaurant.tags)
        holder.b.tvAddress.text = item.restaurant.address
        holder.b.root.setOnClickListener { onClick(item) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<RestaurantWithRating>() {
            override fun areItemsTheSame(a: RestaurantWithRating, b: RestaurantWithRating) =
                a.restaurant.id == b.restaurant.id

            override fun areContentsTheSame(a: RestaurantWithRating, b: RestaurantWithRating) = a == b
        }
    }
}