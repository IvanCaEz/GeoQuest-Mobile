package com.example.geoquest_app.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.ItemReviewBinding

class ReviewAdapter (private val reviews: List<Reviews>, val listener: OnClickListenerReview): RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemReviewBinding.bind(view)
        fun setListener(review: Reviews){
            binding.root.setOnClickListener {
                listener.onClick(review)
            }
        }
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        with(holder){
            setListener(review)
            binding.userName.text = review.idReview.toString()
            binding.comment.text = review.opinion

        }
        }
    }


