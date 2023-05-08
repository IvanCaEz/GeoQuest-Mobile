package com.example.geoquest_app.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.geoquest_app.R

class ReportDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_report, null))
                // Add action buttons
                .setPositiveButton("Publish report",
                    DialogInterface.OnClickListener { dialog, id ->
                        // sign in the user ...
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}