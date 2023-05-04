package com.example.geoquest_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.model.OnClickListenerReview
import com.example.geoquest_app.model.ReviewAdapter
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures


class TreasureDetailFragment : Fragment(), OnClickListenerReview {

    lateinit var binding: FragmentTreasureDetailBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var viewModel: GeoViewModel

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

        viewModel = ViewModelProvider(requireActivity())[GeoViewModel::class.java]
        viewModel.getAllReviews(3)
        viewModel.reviewListData.observe(viewLifecycleOwner){
            setUpRecyclerView(it!!)
        }

        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_treasureDetailFragment_to_startGameFragment)
        }


    }

    private fun setUpRecyclerView(list: List<Reviews>){
        reviewAdapter = ReviewAdapter(list, this)
        linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = reviewAdapter
        }
    }

    override fun onClick(review: Reviews) {
        TODO("Not yet implemented")
    }
}