package com.example.geoquest_app.viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentEndGameBinding
import com.example.geoquest_app.databinding.FragmentStartGameBinding

class EndGameFragment : Fragment() {

    lateinit var binding: FragmentEndGameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEndGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        binding.publish.setOnClickListener {
            findNavController().navigate(R.id.action_endGameFragment_to_listAndSearchFragment)
        }
    }

}