package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentStartGameBinding
import com.example.geoquest_app.view.MainActivity
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Games
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StartGameFragment : Fragment() {

    lateinit var binding: FragmentStartGameBinding
    private val viewModel: GeoViewModel by activityViewModels()

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

        val treasureId = arguments?.getInt("treasureId")!!.toInt()
        viewModel.getTreasureByID(treasureId)
        viewModel.getTreasureImage(treasureId)

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDateString = currentDate.format(formatter)

        viewModel.treasureImage.observe(viewLifecycleOwner){ treasureImage ->
            binding.treasureImg.setImageBitmap(treasureImage)
        }
        viewModel.treasureData.observe(viewLifecycleOwner){ treasure ->
            binding.hint.text = treasure.clue
            binding.treasureName.text = treasure.name
        }

        binding.end.setOnClickListener {
            val currentEndDate = LocalDate.now()
            val endDateString = currentEndDate.format(formatter)
            val game = Games(0, treasureId, 1, true,startDateString,endDateString)
            viewModel.postGame(treasureId, game)
            findNavController().navigate(R.id.action_startGameFragment_to_endGameFragment)
        }
        binding.showHint.setOnClickListener {
            binding.hint.visibility=View.VISIBLE

        }
    }

}