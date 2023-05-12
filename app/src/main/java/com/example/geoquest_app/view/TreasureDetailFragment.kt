package com.example.geoquest_app.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.view.adapters.onClickListeners.OnClickListenerReview
import com.example.geoquest_app.view.adapters.ReviewAdapter
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TreasureDetailFragment : Fragment(), OnClickListenerReview {

    lateinit var binding: FragmentTreasureDetailBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private val viewModel: GeoViewModel by activityViewModels()
    var isFav = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreasureDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        loginAnimation()

        binding.treasureName.isSelected = true
        binding.tresaureStatsValue1.isSelected = true
        binding.tresaureStatsValue2.isSelected = true
        binding.tresaureStatsValue3.isSelected = true
        binding.tresaureStatsValue4.isSelected = true
        binding.tresaureStatsValue5.isSelected = true
        binding.tresaureStatsValue6.isSelected = true

        val treasureID = arguments?.getInt("treasureID")!!
        val userID = viewModel.userData.value?.idUser!!
        viewModel.getTreasureByID(treasureID)
        viewModel.getAllReviews(treasureID)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.checkIfTreasureIsFav(userID, treasureID)
        }

        binding.treasureImg.setImageBitmap(viewModel.treasureImages[treasureID])
        viewModel.treasureData.observe(viewLifecycleOwner) { treasure ->
            setTreasureInfo(treasure)
        }

        viewModel.getTreasureStats(treasureID)

        viewModel.isFav.observe(viewLifecycleOwner) {
            isFav = it
            binding.favorite.isChecked = it
        }

        binding.favorite.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                if (!isFav) {
                    viewModel.addFavTreasure(userID, treasureID)
                    viewModel.checkIfTreasureIsFav(userID, treasureID)
                } else {
                    viewModel.deleteFavTreasure(userID, treasureID)
                    viewModel.checkIfTreasureIsFav(userID, treasureID)
                }
            }
        }
        binding.shimmerViewContainer.visibility = View.INVISIBLE

        viewModel.reviewListData.observe(viewLifecycleOwner) { reviewList ->
            binding.shimmerViewContainer.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                reviewList.forEach { review ->
                    viewModel.getUserByID(review.idUser, "review")
                    viewModel.getUserImage(review.idUser)
                }
                withContext(Dispatchers.Main){
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    setUpRecyclerView(reviewList!!)
                }
            }

        }



        binding.play.setOnClickListener {
            val distance = viewModel.distanceMapVM.value?.get(treasureID)
            if (distance!! <= 1000) {
                val toPlay =
                    TreasureDetailFragmentDirections.actionTreasureDetailFragmentToStartGameFragment(
                        treasureID
                    )
                findNavController().navigate(toPlay)
            }else {
                Toast.makeText(
                    requireContext(),
                    "Get closer to the treasure to start the hunt",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.report.setOnClickListener {
            showDialogReport()
        }

    }

    private fun loginAnimation(){
        val slideConstraint = binding.treasureDetailSlideContainer
        val mainText = binding.treasureName
        val firstEditText = binding.treasureImg
        val secondEditText = binding.ratingBar
        val firstTextSignUp = binding.dificulty
        val secondTextSignUp = binding.horizontalScrollview
        val logoImage = binding.play
        val favourite = binding.favorite
        val report = binding.report

        mainText.alpha = 0.0f
        firstEditText.alpha = 0.0f
        secondEditText.alpha = 0.0f
        firstTextSignUp.alpha = 0.0f
        secondTextSignUp.alpha = 0.0f
        logoImage.alpha = 0.0f
        favourite.alpha = 0.0f
        report.alpha = 0.0f

        slideConstraint.y = -2200.0f
        slideConstraint.animate().translationY(0.0f).duration = 1500
        Handler(Looper.getMainLooper()).postDelayed({
            mainText.animate().alpha(1.0f).duration = 350
            firstEditText.animate().alpha(1.0f).duration = 350
            secondEditText.animate().alpha(1.0f).duration = 350
            firstTextSignUp.animate().alpha(1.0f).duration = 350
            secondTextSignUp.animate().alpha(1.0f).duration = 350
            logoImage.animate().alpha(1.0f).duration = 350
            favourite.animate().alpha(1.0f).duration = 350
            report.animate().alpha(1.0f).duration = 350
        }, 1800)
    }
    fun setUpRecyclerView(list: List<Reviews>) {
        reviewAdapter = ReviewAdapter(list, this, viewModel)
        linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = reviewAdapter
        }
    }
    private fun setTreasureInfo(treasure: Treasures) {
        binding.treasureName.text = treasure.name
        binding.dificulty.text = treasure.difficulty
        binding.ratingBar.rating = treasure.score.toFloat()
        viewModel.treasureStats.observe(viewLifecycleOwner){treasureStats ->
            binding.reportQuantity.text = treasureStats.reportQuantity.toString()
            binding.averageTime.text = treasureStats.averageTime
            binding.favQuantity.text = treasureStats.totalFavourite.toString()
            binding.reviewQuantity.text = treasureStats.totalReviews.toString()
            binding.solvedTreasures.text = treasureStats.totalFound.toString()
            val notSolved =  treasureStats.totalPlayed - treasureStats.totalFound
            binding.notSolvedTreasures.text = notSolved.toString()
            binding.notSolvedTreasures.visibility = View.VISIBLE
            binding.averageTime.visibility = View.VISIBLE
            binding.reportQuantity.visibility = View.VISIBLE
            binding.reviewQuantity.visibility = View.VISIBLE
            binding.solvedTreasures.visibility = View.VISIBLE
            binding.favQuantity.visibility = View.VISIBLE
        }
    }
    fun showDialogReport() {
        val dialog = ReportDialog()
        dialog.show(parentFragmentManager, "report")
    }

    override fun onClick(review: Reviews) {

    }


}