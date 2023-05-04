package com.example.geoquest_app.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.ItemReviewBinding
import com.example.geoquest_app.databinding.ItemTreasuresBinding
import com.example.models.Treasures

class TreasureAdapter (private val treasures: List<Treasures>, val listener: OnClickListenerTreasure): RecyclerView.Adapter<TreasureAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTreasuresBinding.bind(view)
        fun setListener(treasure: Treasures){
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
        with(holder){
            setListener(treasure)
            binding.treasureName.text = treasure.name
            binding.difficulty.text = treasure.difficulty
            binding.score.text = treasure.score.toString()
            Glide.with(context)
                .load(treasure.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.treasureImg)
        }
        }
    }


