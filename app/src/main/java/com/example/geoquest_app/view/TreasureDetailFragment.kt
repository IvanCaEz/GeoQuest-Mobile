package com.example.geoquest_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.model.UserAdapter
import com.example.geoquest_app.model.review
import com.example.geoquest_app.view.MainActivity

class TreasureDetailFragment : Fragment() {

    lateinit var binding: FragmentTreasureDetailBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentTreasureDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)
        userAdapter = UserAdapter(getUsers())
        linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            setHasFixedSize(true) //Optimitza el rendiment de l’app
            layoutManager = linearLayoutManager
            adapter = userAdapter
        }


        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_treasureDetailFragment_to_startGameFragment)
        }


    }
    private fun getUsers(): MutableList<review>{
        val users = mutableListOf<review>()
        users.add(review("Joel", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        users.add(review("Alejandro", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        users.add(review("Ivan", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        users.add(review("Marti", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        users.add(review("Raul", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        users.add(review("Asier", "", "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"))
        return users
    }


}