package com.example.geoquest_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentProfileBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerReviewUser
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.adapters.UserProfileReviewAdapter
import com.example.geoquest_app.model.ReviewDialogUpdate
import com.example.geoquest_app.viewmodel.GeoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // ANIMATION DE ENTRADA

        val slideConstraint = binding.profileSlideContainer
        val profileTitle = binding.profileTitleTextview
        val userImg = binding.userImageIV
        val editProfile = binding.editProfile
        val profileName = binding.profileName
        val userStats1 = binding.userStats1
        val userStats2 = binding.userStats2
        val userStats3 = binding.userStats3
        val userStats4 = binding.userStats4
        val reviewText = binding.reviewText
        val shimmer = binding.shimmerViewContainerProfile
        val recyclerView = binding.recyclerView

        binding.emptyList.alpha = 0.0f
        profileTitle.alpha = 0.0f
        userImg.alpha = 0.0f
        editProfile.alpha = 0.0f
        profileName.alpha = 0.0f
        userStats1.alpha = 0.0f
        userStats2.alpha = 0.0f
        userStats3.alpha = 0.0f
        userStats4.alpha = 0.0f
        reviewText.alpha = 0.0f
        recyclerView.alpha = 0.0f
        shimmer.alpha = 0.0f

        viewModel.getUserStats(id) // do not work
        val solvedTreasures = binding.solvedTreasures
        val notSolvedTreasures = binding.notSolvedTreasures
        val reportQuantity = binding.reportQuantity
        val averageTime = binding.averageTime

        viewModel.userStats.observe(viewLifecycleOwner) { userStats ->
            solvedTreasures.text = userStats.solved.toString()
            notSolvedTreasures.text = userStats.notSolved.toString()
            reportQuantity.text = userStats.reportQuantity.toString()
            averageTime.text = userStats.averageTime
        }
        binding.profileName.text = viewModel.userData.value?.nickName

        slideConstraint.y = -2200f
        slideConstraint.animate().translationY(0.0f).duration = 1500
        Handler(Looper.getMainLooper()).postDelayed({
            userImg.animate().alpha(1.0f).duration = 350
            profileTitle.animate().alpha(1.0f).duration = 350
            editProfile.animate().alpha(1.0f).duration = 350
            profileName.animate().alpha(1.0f).duration = 350
            userStats4.animate().alpha(1.0f).duration = 350
            userStats3.animate().alpha(1.0f).duration = 350
            userStats2.animate().alpha(1.0f).duration = 350
            userStats1.animate().alpha(1.0f).duration = 350
            reviewText.animate().alpha(1.0f).duration = 350
            shimmer.animate().alpha(1.0f).duration = 350
            recyclerView.animate().alpha(1.0f).duration = 350
        }, 1800)

        val userId = viewModel.userData.value!!.idUser
        averageTime.isSelected = true

        shimmer.visibility = View.VISIBLE
        binding.recyclerView.alpha = 0.0f

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getUserImage(userId)
        }
        viewModel.userImage.observe(viewLifecycleOwner) { userImage ->
            binding.userImageIV.setImageBitmap(userImage)
        }
        viewModel.getReviewsByUserId(userId)
        viewModel.userReviews.observe(viewLifecycleOwner) { reviews ->
            if (reviews.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    reviews.forEach { review ->
                        viewModel.getTreasureByID(review.idTreasure)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.emptyList.visibility = View.INVISIBLE
                        binding.shimmerViewContainerProfile.visibility = View.GONE
                        binding.recyclerView.animate().alpha(1.0f).duration = 400
                        setUpRecyclerView(reviews.toMutableList())
                    }, 1700)
                }
            } else {
                binding.emptyList.visibility = View.VISIBLE
                binding.shimmerViewContainerProfile.visibility = View.INVISIBLE
                binding.recyclerView.animate().alpha(1.0f).duration = 400
                setUpRecyclerView(reviews.toMutableList())
            }
        }
        binding.editProfile.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }
    private fun setUpRecyclerView(reviewList: MutableList<Reviews>) {
        reviewAdapter = UserProfileReviewAdapter(viewModel, reviewList, this)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = reviewAdapter
        }
    }

    override fun onClick(review: Reviews) {
        val dialog = ReviewDialogUpdate(review)
        dialog.show(parentFragmentManager, "review")
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

}