package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentListAndSearchBinding
import com.example.geoquest_app.model.OnClickListenerTreasure
import com.example.geoquest_app.model.ReviewAdapter
import com.example.geoquest_app.model.TreasureAdapter
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures

class ListAndSearchFragment : Fragment(), OnClickListenerTreasure {

    private lateinit var treasureAdapter: TreasureAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var viewModel: GeoViewModel
    lateinit var binding: FragmentListAndSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentListAndSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        viewModel = ViewModelProvider(requireActivity())[GeoViewModel::class.java]
        viewModel.getAllTreasures()
        viewModel.treasureListData.observe(viewLifecycleOwner){
            setUpRecyclerView(it!!)
        }
    }

    private fun setUpRecyclerView(treasureList: List<Treasures>){
        treasureAdapter = TreasureAdapter(treasureList, this)
        linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = treasureAdapter
        }
    }

    override fun onClick(treasures: Treasures) {
        val action = ListAndSearchFragmentDirections.actionListAndSearchFragmentToTreasureDetailFragment(treasures.idTreasure)
        findNavController().navigate(action)
    }

}