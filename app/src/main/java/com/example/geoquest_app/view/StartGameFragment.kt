package com.example.geoquest_app.view

import android.annotation.SuppressLint
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
import com.example.models.Treasures
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Runnable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StartGameFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var binding: FragmentStartGameBinding
    private val viewModel: GeoViewModel by activityViewModels()
    lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentCoordinates: LatLng
    var route: Polyline? = null
    private val handler = Handler()
    private lateinit var runnable: Runnable

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isLocationPermissionGranted()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location = it.result
                if (location != null) {
                    currentCoordinates = LatLng(location.latitude, location.longitude)
                    createMap()
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartGameBinding.inflate(layoutInflater)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)

        val treasureID = arguments?.getInt("treasureID")!!

        viewModel.getTreasureByID(treasureID)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getTreasureImage(treasureID)
        }

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




        binding.end.setOnClickListener {
            val currentEndDate = LocalDateTime.now()
            val endDateString = currentEndDate.format(formatter)
            val toEndGame = StartGameFragmentDirections
                .actionStartGameFragmentToEndGameFragment(startDateString,endDateString, treasureID)
            findNavController().navigate(toEndGame)
        }
        binding.showHint.setOnClickListener {
            binding.hint.visibility=View.VISIBLE
        }
    }

    fun createMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_little) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(currentCoordinates, 14f),
            1500, null
        )

        viewModel.treasureData.observe(viewLifecycleOwner){ treasure ->
            binding.hint.text = treasure.clue
            binding.treasureName.text = treasure.name
            val coordinates = LatLng(treasure.latitude, treasure.longitude)
            val treasureMarker = MarkerOptions().position(coordinates).title(treasure.name)
            map.addMarker(treasureMarker)
            route?.remove()
            val start = "${currentCoordinates.longitude},${currentCoordinates.latitude}"
            val end = "${treasureMarker.position.longitude},${treasureMarker.position.latitude}"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getRoute("5b3ce3597851110001cf624877a97a68b1a84fa2bcb01fb0aa655b89", start, end)
                withContext(Dispatchers.Main){
                    drawRoute()
                }
            }
        }


        map.setOnMarkerClickListener(this)

    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        val REQUEST_CODE_LOCATION = 100
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                requireContext(),
                "Ves a la pantalla de permisos de l’aplicació i habilita el de Geolocalització",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val REQUEST_CODE_LOCATION = 100
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    requireContext(), "Accepta els permisos de geolocalització",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMarkerClick(treasureMarker: Marker): Boolean {
        return false
    }

    private fun drawRoute() {
        val polyLineOptions = PolylineOptions()
            .startCap(RoundCap())
            .endCap(RoundCap())
            .width(12f)
            .color(ContextCompat.getColor(requireContext(), R.color.ocre))
        viewModel.route.observe(viewLifecycleOwner) { routeResponse ->
            // devuelve las coordenadas alreves
            routeResponse.features.first().geometry.coordinates.forEach {
                polyLineOptions.add(LatLng(it[1], it[0]))
            }
        }
        route = map.addPolyline(polyLineOptions)
        route!!.isClickable = true
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }





}