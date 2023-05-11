package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentLogOutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LogOutFragment : Fragment() {
    private lateinit var binding: FragmentLogOutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogOutBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you really sure you want to log out?")
            .setPositiveButton("Yes, I'm sure") { dialog, which ->
                val toLogin = LogOutFragmentDirections.actionLogOutFragmentToLogInFragment()
                findNavController().navigate(toLogin)
            }
            .setNegativeButton("Take me back"){ dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

}