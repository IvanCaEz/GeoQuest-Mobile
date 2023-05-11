package com.example.geoquest_app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentLogOutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.geoquest_app.viewmodel.GeoViewModel

class LogOutFragment : Fragment() {
    private lateinit var binding: FragmentLogOutBinding
    val viewModel: GeoViewModel by activityViewModels()
    lateinit var myPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you really sure you want to log out?")
            .setPositiveButton("Yes, I'm sure") { dialog, which ->
                myPreferences =
                    requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

                myPreferences.edit {
                    putString("userName", "")
                    putString("password", "")
                    putString("token", "")
                    putBoolean("active", false)
                    apply()
                }
                viewModel.userData.postValue(null)
                viewModel.updateRepository("")

                findNavController().navigate(LogOutFragmentDirections.actionLogOutFragmentToLogInFragment())
            }
            .setNegativeButton("Take me back"){ dialog, which ->
                findNavController().navigate(LogOutFragmentDirections.actionLogOutFragmentToMapFragment())
                dialog.dismiss()
            }
            .show()





    }
}
