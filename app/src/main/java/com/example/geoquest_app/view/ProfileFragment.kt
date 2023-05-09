package com.example.geoquest_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentProfileBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerReviewUser
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.adapters.UserProfileReviewAdapter
import com.example.geoquest_app.utils.ReportDialog
import com.example.geoquest_app.viewmodel.GeoViewModel

class ProfileFragment : Fragment(), OnClickListenerReviewUser {

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

        val userStats = viewModel.getUserStats(id) // do not work
        val solvedTreasures = binding.solvedTreasuresTitle
        val notSolvedTreasures = binding.notSolvedTreasuresTitle
        val reportQuantity = binding.reportQuantityTitle
        val averageTime = binding.averageTimeTitle

        solvedTreasures.isSelected = true
        notSolvedTreasures.isSelected = true
        reportQuantity.isSelected = true
        averageTime.isSelected = true


        binding.shimmerViewContainerProfile.visibility = View.VISIBLE
        binding.recyclerView.alpha = 0.0f
        viewModel.getUserImage(id)
        viewModel.getReviewsByUserId(id)

        viewModel.userImage.observe(viewLifecycleOwner){ userImage ->

            binding.userImageIV.setImageBitmap(userImage)
        }

        viewModel.userReviews.observe(viewLifecycleOwner){
            it.forEach{ review ->
                viewModel.getTreasureByID(review.idTreasure)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                binding.shimmerViewContainerProfile.visibility = View.GONE
                binding.recyclerView.animate().alpha(1.0f).duration = 400
                setUpRecyclerView(it!!.toMutableList())
            }, 1700)
        }

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun setUpRecyclerView(reviewList: MutableList<Reviews>){
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

    override fun onDelete(review: Reviews) {
        viewModel.deleteReviewByTreasureId(review.idTreasure, review.idReview)
        reviewAdapter.delete(review)
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainerProfile.visibility = View.VISIBLE
        binding.recyclerView.alpha = 0.0f
    }


    private fun setUpDialog(){
        val dialog = ReportDialog()
        dialog.show(parentFragmentManager, "report")
    }

}