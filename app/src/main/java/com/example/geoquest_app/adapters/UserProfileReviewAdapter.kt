package com.example.geoquest_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.ItemReviewsUserBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerReviewUser
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel

class UserProfileReviewAdapter(private val viewModel: GeoViewModel, private val reviews: MutableList<Reviews>, val listener: OnClickListenerReviewUser): RecyclerView.Adapter<UserProfileReviewAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemReviewsUserBinding.bind(view)
        fun setListener(review: Reviews){
            binding.root.setOnClickListener {
                listener.onClick(review)
            }
        }
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_reviews_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        with(holder){
            setListener(review)
            binding.reviewTreasureTitleTextView.isSelected = true
            binding.reviewTreasureTitleTextView.text = viewModel.treasureName[review.idTreasure].toString()
            binding.ratingReviewUserTextView.text = review.rating.toString()
        }
    }

    fun delete(review: Reviews) {
        val index = reviews.indexOf(review)
        if(index != -1){
            reviews.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}