package com.example.geoquest_app.model

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.geoquest_app.databinding.DialogReportBinding
import com.example.geoquest_app.databinding.DialogReviewUpdateBinding
import com.example.geoquest_app.viewmodel.GeoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReviewDialogUpdate(val review: Reviews): DialogFragment() {
    lateinit var binding: DialogReviewUpdateBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val viewModel: GeoViewModel by activityViewModels()
            val userID = viewModel.userData.value?.idUser
            val builder = AlertDialog.Builder(it)

            binding = DialogReviewUpdateBinding.inflate(layoutInflater)

            binding.treasureName.text = viewModel.treasureName[review.idTreasure].toString()
            binding.opinionUpdate.editText?.hint = review.opinion
            binding.ratingUpdate.editText?.hint = review.rating.toString()

            builder.setView(binding.root)
                // Add action buttons
                .setPositiveButton("Update") { dialog, id ->
                    //val review = Reviews()
                    //viewModel.updateReviewByTreasureId(review, )
                    Toast.makeText(requireContext(), "Review updated", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}