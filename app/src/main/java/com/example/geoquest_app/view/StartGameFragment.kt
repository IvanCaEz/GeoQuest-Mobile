package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentStartGameBinding
import com.example.geoquest_app.view.MainActivity

class StartGameFragment : Fragment() {

    lateinit var binding: FragmentStartGameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        binding.end.setOnClickListener {
            findNavController().navigate(R.id.action_startGameFragment_to_endGameFragment)
        }
        binding.showHint.setOnClickListener {
            binding.hint.visibility=View.VISIBLE
        }
    }

}