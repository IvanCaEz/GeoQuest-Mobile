package com.example.geoquest_app.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.ItemTreasuresBinding
import com.example.geoquest_app.view.adapters.onClickListeners.OnClickListenerTreasure
import com.example.geoquest_app.utils.round
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures
import kotlin.math.round

class TreasureAdapter(
    private val treasures: List<Treasures>,
    val listener: OnClickListenerTreasure,
    val viewModel: GeoViewModel
) : RecyclerView.Adapter<TreasureAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTreasuresBinding.bind(view)
        fun setListener(treasure: Treasures) {
            binding.root.setOnClickListener {
                listener.onClick(treasure)
            }
        }
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_treasures, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return treasures.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val treasure = treasures[position]
        with(holder) {
            setListener(treasure)
            binding.treasureTitleTV.text = treasure.name
            binding.difficultyTV.text = "Level: " +treasure.difficulty
            binding.scoreTV.text = treasure.score.round(2).toString()
            binding.locationTag.text = treasure.location
            binding.treasureIV.setImageBitmap(viewModel.treasureImages[treasure.idTreasure])

        }
    }


}


