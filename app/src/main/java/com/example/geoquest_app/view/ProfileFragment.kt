package com.example.geoquest_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentProfileBinding
import com.example.geoquest_app.model.OnClickListenerReview
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.model.UserProfileReviewAdapter
import com.example.geoquest_app.viewmodel.GeoViewModel

class ProfileFragment : Fragment(), OnClickListenerReview {
    lateinit var binding: FragmentProfileBinding
    private val viewModel: GeoViewModel by activityViewModels()
    private lateinit var reviewAdapter: UserProfileReviewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        val id = viewModel.userData.value!!.idUser
        viewModel.getReviewsByUserId(id)
        viewModel.userReviews.observe(viewLifecycleOwner){
            it.forEach{ review ->
                viewModel.getTreasureByID(review.idTreasure)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                setUpRecyclerView(it!!)
            }, 1700)
        }

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun setUpRecyclerView(reviewList: List<Reviews>){
        reviewAdapter = UserProfileReviewAdapter(viewModel, reviewList, this)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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