package com.example.geoquest_app.view

import android.annotation.SuppressLint
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
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentListAndSearchBinding
import com.example.geoquest_app.adapters.onClickListeners.OnClickListenerTreasure
import com.example.geoquest_app.adapters.TreasureAdapter
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Favourites
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
    var favTreasureList = listOf<Treasures>()
    var favList = listOf<Favourites>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListAndSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)
        //Comienza con el botón Todos checkeado
        binding.toggleButton.check(R.id.all_markers_button)

        viewModel.treasureListData.observe(viewLifecycleOwner) { treasureListVM ->
            binding.recyclerView.visibility = View.INVISIBLE
            treasureList = treasureListVM
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.nofavsTV.visibility = View.INVISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                treasureListVM.forEach { treasure ->
                    viewModel.getTreasureImage(treasure.idTreasure)
                }
                withContext(Dispatchers.Main) {
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                        setUpRecyclerView(treasureList)
                    } else {
                        setAdapter(true)
                    }
                }
            }
        }

        viewModel.userFavs.observe(viewLifecycleOwner) { favListVM ->
            favList = favListVM
        }
        binding.swipelayout.setColorSchemeColors(R.color.color1, R.color.marronOscuro)
        // SWIPE
       // esto cuando acabe de hacer las llamads binding.swipelayout.isEnabled = false
        binding.swipelayout.setOnRefreshListener {
            binding.swipelayout.isRefreshing = false
            if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                viewModel.getAllTreasures()
            } else {
                binding.recyclerView.visibility = View.INVISIBLE
                viewModel.getUserFavs(viewModel.userData.value!!.idUser)
            }
        }



        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.all_markers_button -> {
                        viewModel.getAllTreasures()
                        setAdapter(false)
                    }
                    R.id.favourites_button ->{
                        viewModel.getUserFavs(viewModel.userData.value!!.idUser)
                        setAdapter(true)
                    }
                }
            } else {
                if (toggleButton.checkedButtonId == View.NO_ID) setAdapter(false)
            }
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val filter = binding.searchSpinner.selectedItem.toString()
                    var filterList = listOf<Treasures>()
                    when (filter) {
                        "Location" -> {
                            filterList =
                                if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                                    treasureList.filter {
                                        it.location.lowercase(Locale.ROOT)
                                            .contains(query.lowercase(Locale.ROOT))
                                    }
                                } else favTreasureList.filter {
                                    it.location.lowercase(Locale.ROOT)
                                        .contains(query.lowercase(Locale.ROOT))
                                }
                        }
                        "Score" -> {
                            filterList =
                                if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                                    treasureList.filter { it.score.toString() >= query }
                                } else favTreasureList.filter { it.score.toString() >= query }
                        }
                        "Name" -> {
                            filterList =
                                if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                                    treasureList.filter {
                                        it.name.lowercase(Locale.ROOT)
                                            .contains(query.lowercase(Locale.ROOT))
                                    }
                                } else favTreasureList.filter {
                                    it.name.lowercase(Locale.ROOT)
                                        .contains(query.lowercase(Locale.ROOT))
                                }
                        }
                        "Difficulty" -> {
                            filterList =
                                if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                                    treasureList.filter {
                                        it.difficulty.lowercase(Locale.ROOT)
                                            .contains(query.lowercase(Locale.ROOT))
                                    }
                                } else favTreasureList.filter {
                                    it.difficulty.lowercase(Locale.ROOT)
                                        .contains(query.lowercase(Locale.ROOT))
                                }
                        }
                        else -> {}
                    }
                    setUpRecyclerView(filterList)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.equals("")) {
                    this.onQueryTextSubmit("")
                } else {
                    if (binding.toggleButton.checkedButtonId == R.id.all_markers_button) {
                        setUpRecyclerView(treasureList)
                    } else setUpRecyclerView(favTreasureList)
                }
                return true
            }
        })
    }

    private fun setAdapter(filterVisited: Boolean) {
        binding.nofavsTV.visibility = View.INVISIBLE
        if (filterVisited) {
            binding.shimmerViewContainer.visibility = View.INVISIBLE
            val treasureIDs = mutableListOf<Int>()
            if (favList.isNotEmpty()) {
                binding.nofavsTV.visibility = View.INVISIBLE
                favList.forEach { treasureIDs.add(it.idTreasure) }
                favTreasureList = treasureList.filter { treasureIDs.contains(it.idTreasure) }
                setUpRecyclerView(favTreasureList)
            } else {
                binding.nofavsTV.visibility = View.VISIBLE
                setUpRecyclerView(favTreasureList)
            }

        } else {
            binding.nofavsTV.visibility = View.INVISIBLE
            setUpRecyclerView(treasureList)
        }
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
        val action = ListAndSearchFragmentDirections
            .actionListAndSearchFragmentToTreasureDetailFragment(treasures.idTreasure)
        findNavController().navigate(action)
    }
}