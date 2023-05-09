package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentLogOutBinding

class LogOutFragment : Fragment() {
    private lateinit var binding: FragmentLogOutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // vaciar las prefs y userData.postValue(null)
        val toLogin = LogOutFragmentDirections.actionLogOutFragmentToLogInFragment()
        findNavController().navigate(toLogin)
    }

}