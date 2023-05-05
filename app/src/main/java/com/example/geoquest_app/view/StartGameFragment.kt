package com.example.geoquest_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.databinding.FragmentStartGameBinding
import com.example.geoquest_app.view.MainActivity
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Games
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StartGameFragment : Fragment() {

    lateinit var binding: FragmentStartGameBinding
    private val viewModel: GeoViewModel by activityViewModels()
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private var secondsElapsed = 0
    private val handler = Handler()
    private lateinit var runnable: Runnable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        val treasureId = arguments?.getInt("treasureId")!!.toInt()
        viewModel.getTreasureByID(treasureId)
        viewModel.getTreasureImage(treasureId)


        val startTime = System.currentTimeMillis()

        runnable = Runnable {
            val elapsedTime = System.currentTimeMillis() - startTime
            val seconds = (elapsedTime / 1000) % 60
            val minutes = (elapsedTime / (1000 * 60)) % 60
            val hours = (elapsedTime / (1000 * 60 * 60))

            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            binding.time.text = timeString

            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable, 0)

        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss")
        val startDateString = currentDate.format(formatter)

        viewModel.treasureImage.observe(viewLifecycleOwner){ treasureImage ->
            binding.treasureImg.setImageBitmap(treasureImage)
        }
        viewModel.treasureData.observe(viewLifecycleOwner){ treasure ->
            binding.hint.text = treasure.clue
            binding.treasureName.text = treasure.name
        }



        binding.end.setOnClickListener {

            val currentEndDate = LocalDateTime.now()
            val endDateString = currentEndDate.format(formatter)
            val toEndGame = StartGameFragmentDirections
                .actionStartGameFragmentToEndGameFragment(startDateString,endDateString, treasureId)
            findNavController().navigate(toEndGame)
        }
        binding.showHint.setOnClickListener {
            binding.hint.visibility=View.VISIBLE

        }
    }




}