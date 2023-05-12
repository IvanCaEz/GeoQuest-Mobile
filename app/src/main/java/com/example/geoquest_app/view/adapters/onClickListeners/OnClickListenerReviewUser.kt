package com.example.geoquest_app.view.adapters.onClickListeners

import com.example.geoquest_app.model.Reviews

interface OnClickListenerReviewUser {
    fun onClick(review: Reviews)
    fun onDelete(review: Reviews)
}