package com.example.restaurantguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantguide.databinding.ItemReviewBinding

// One row in a review list. restaurantName is only used on the Home screen.
data class ReviewRow(
    val id: Long,
    val restaurantName: String?,
    val rating: Int,
    val comment: String,
    val visitDate: Long
)

fun Review.toRow() = ReviewRow(id, null, rating, comment, visitDate)
fun ReviewWithRestaurant.toRow() = ReviewRow(id, restaurantName, rating, comment, visitDate)

class ReviewAdapter(
    private val onLongClick: (ReviewRow) -> Unit = {}
) : ListAdapter<ReviewRow, ReviewAdapter.VH>(DIFF) {

    class VH(val b: ItemReviewBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvTitle.visibility = if (item.restaurantName == null) View.GONE else View.VISIBLE
        holder.b.tvTitle.text = item.restaurantName.orEmpty()
        holder.b.tvStars.text = starsText(item.rating)
        holder.b.tvComment.visibility = if (item.comment.isBlank()) View.GONE else View.VISIBLE
        holder.b.tvComment.text = item.comment
        holder.b.tvDate.text = formatDate(item.visitDate)
        holder.b.root.setOnLongClickListener { onLongClick(item); true }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ReviewRow>() {
            override fun areItemsTheSame(a: ReviewRow, b: ReviewRow) = a.id == b.id
            override fun areContentsTheSame(a: ReviewRow, b: ReviewRow) = a == b
        }
    }
}