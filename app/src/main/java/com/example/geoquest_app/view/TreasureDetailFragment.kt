package com.example.geoquest_app.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerReview
import com.example.geoquest_app.adapters.ReviewAdapter
import com.example.geoquest_app.model.Reviews
import com.example.geoquest_app.utils.ReportDialog
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

        val treasureID = arguments?.getInt("treasureID")!!
        val userID = viewModel.userData.value?.idUser!!
        viewModel.getTreasureByID(treasureID)
        viewModel.getAllReviews(treasureID)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getTreasureImage(treasureID)
        }
        viewModel.checkIfTreasureIsFav(userID, treasureID)
        viewModel.getTreasureStats(treasureID)

        viewModel.isFav.observe(viewLifecycleOwner) {
            isFav = it
            binding.favorite.isChecked = isFav
        }

        binding.favorite.setOnClickListener {
            if (!isFav) {
                viewModel.addFavTreasure(userID, treasureID)
                viewModel.checkIfTreasureIsFav(userID, treasureID)
            } else {
                viewModel.deleteFavTreasure(userID, treasureID)
                viewModel.checkIfTreasureIsFav(userID, treasureID)
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

        viewModel.treasureData.observe(viewLifecycleOwner) { treasure ->
            setTreasureInfo(treasure)
        }

        viewModel.treasureImage.observe(viewLifecycleOwner) { treasureImage ->
            binding.treasureImg.setImageBitmap(treasureImage)
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
        binding.location.text = treasure.location
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