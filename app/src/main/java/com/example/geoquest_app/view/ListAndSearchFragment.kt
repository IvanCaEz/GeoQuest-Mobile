package com.example.geoquest_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.databinding.FragmentListAndSearchBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerTreasure
import com.example.geoquest_app.adapters.TreasureAdapter
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ListAndSearchFragment : Fragment(), OnClickListenerTreasure {

    private lateinit var treasureAdapter: TreasureAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    lateinit var binding: FragmentListAndSearchBinding
    private val viewModel: GeoViewModel by activityViewModels()
    var treasureList = listOf<Treasures>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListAndSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)


        viewModel.getAllTreasures()
        viewModel.treasureListData.observe(viewLifecycleOwner) { treasureListVM ->
            treasureList = treasureListVM
            binding.shimmerViewContainer.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch{
                treasureListVM.forEach { treasure ->
                    viewModel.getTreasureImage(treasure.idTreasure)
                }
                withContext(Dispatchers.Main){
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    setUpRecyclerView(treasureList)
                }
            }


           // Handler(Looper.getMainLooper()).postDelayed({

           // },2000)

        }




        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val filter = binding.searchSpinner.selectedItem.toString()
                    var filterList = listOf<Treasures>()
                   when(filter){
                       "Location" ->{
                           filterList = treasureList.filter { it.location.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) }
                       }
                       "Score" -> {
                           filterList =  treasureList.filter { it.score.toString() >= query}
                       }
                       "Name" -> {
                           filterList =  treasureList.filter { it.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) }
                       }
                       "Difficulty" -> {
                           filterList =  treasureList.filter { it.difficulty.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) }
                       }
                       else -> {  }
                   }
                    setUpRecyclerView(filterList)
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.equals("")){
                    this.onQueryTextSubmit("")
                } else {
                   setUpRecyclerView(treasureList)
                }
                return true

            }

        })
    }





    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView(treasureList: List<Treasures>) {
        treasureAdapter = TreasureAdapter(treasureList, this, viewModel)
        linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = treasureAdapter
        }
    }

    override fun onClick(treasures: Treasures) {
        println("ID TESORO : ${treasures.idTreasure}")

        val action =
            ListAndSearchFragmentDirections.actionListAndSearchFragmentToTreasureDetailFragment(
                treasures.idTreasure
            )
        findNavController().navigate(action)
    }

}