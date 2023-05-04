package com.example.geoquest_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentTreasureDetailBinding
import com.example.geoquest_app.model.UserAdapter


class TreasureDetailFragment : Fragment() {

    lateinit var binding: FragmentTreasureDetailBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
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

    private fun getUsers(): MutableList<Review> {
        val users = mutableListOf<Review>()
        users.add(
            Review(
                "Joel",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        users.add(
            Review(
                "Alejandro",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        users.add(
            Review(
                "Ivan",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        users.add(
            Review(
                "Marti",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        users.add(
            Review(
                "Raul",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        users.add(
            Review(
                "Asier",
                "",
                "Lorem ipsum dolor sit amet consectetur adipiscing, elit ultrices facilisis non risus, sem quis sollicitudin nulla blandit. Ad vulputate a cursus tellus parturient porttitor ut est et, aptent eros congue vivamus dis arcu sem blandit semper odio"
            )
        )
        return users
    }
}