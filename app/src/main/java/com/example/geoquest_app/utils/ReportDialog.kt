package com.example.geoquest_app.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.DialogReportBinding
import com.example.geoquest_app.model.Reports
import com.example.geoquest_app.viewmodel.GeoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReportDialog : DialogFragment() {
    lateinit var binding: DialogReportBinding
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val viewModel: GeoViewModel by activityViewModels()
            val treasureID = viewModel.treasureData.value?.idTreasure
            val userID = viewModel.userData.value?.idUser
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater

            binding = DialogReportBinding.inflate(layoutInflater)

            binding.treasureName.text = viewModel.treasureData.value?.name

            //val inflater = requireActivity().layoutInflater


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(binding.root)
                // Add action buttons
                .setPositiveButton("Publish") { dialog, id ->
                    val report = Reports(
                        0,
                        userID!!,
                        treasureID!!,
                        binding.report.editText!!.text.toString(),
                        formatter.format(LocalDate.now())
                    )
                    viewModel.postReport(report)
                    Toast.makeText(requireContext(), "Report published", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}