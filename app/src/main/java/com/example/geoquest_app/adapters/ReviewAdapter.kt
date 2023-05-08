package com.example.geoquest_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.ItemReviewBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerReview
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel

class ReviewAdapter (private val reviews: List<Reviews>,
                     val listener: OnClickListenerReview,
                     val viewModel: GeoViewModel
                     ): RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
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
            binding.userTV.text = viewModel.userNames[review.idUser]
            binding.reviewBox.text = review.opinion
            binding.userIV.setImageBitmap(viewModel.userImages[review.idUser])
        }
        }
    }


