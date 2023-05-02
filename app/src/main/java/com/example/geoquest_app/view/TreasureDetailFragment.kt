package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.view.MainActivity

class TreasureDetailFragment : Fragment() {

    lateinit var binding: FragmentTreasureDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTreasureDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_treasureDetailFragment_to_startGameFragment)
        }
    }

}