package com.example.geoquest_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.model.UserAdapter
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures


class TreasureDetailFragment : Fragment() {

    lateinit var binding: FragmentTreasureDetailBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    val viewModel: GeoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreasureDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)
        userAdapter = UserAdapter(listOf())
        linearLayoutManager = LinearLayoutManager(context)

        viewModel.getTreasureByID(1)
        viewModel.getTreasureImage(1)

        viewModel.treasureData.observe(viewLifecycleOwner){ treasure ->
            setTreasureInfo(treasure)
        }
        viewModel.treasureImage.observe(viewLifecycleOwner){ image ->
            binding.treasureImg.setImageBitmap(image)

        }





        binding.recyclerView.apply {
            setHasFixedSize(true) //Optimitza el rendiment de l’app
            layoutManager = linearLayoutManager
            adapter = userAdapter

        }


        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_treasureDetailFragment_to_startGameFragment)
        }


    }
    fun setTreasureInfo(treasure: Treasures){
        binding.treasureName.text = treasure.name
        binding.dificulty.text = treasure.difficulty
        binding.location.text = treasure.location
        binding.ratingBar.rating = treasure.score.toFloat()
    }

}