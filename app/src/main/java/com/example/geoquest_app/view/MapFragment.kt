package com.example.geoquest_app.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.geoquest_app.R
import com.example.geoquest_app.view.adapters.MarkerInfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.example.geoquest_app.databinding.FragmentMapBinding
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener,
    OnInfoWindowClickListener {
    lateinit var binding: FragmentMapBinding
    lateinit var map: GoogleMap
    val viewModel: GeoViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentCoordinates: LatLng
    var route: Polyline? = null
    var distanceMap = mutableMapOf<Int, Int>()



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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigationVisible(true)
        viewModel.getAllTreasures()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val locationResults = geocoder.getFromLocationName(query, 1)
                        if (locationResults!!.isNotEmpty()) {
                            val searchCoordinates =
                                LatLng(locationResults[0].latitude, locationResults[0].longitude)
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(searchCoordinates, 12f),
                                2000, null
                            )
                        }
                    } catch (e: IOException) {
                        Log.e("Error ", e.message, e.cause)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun createMarkers(treasureList: List<Treasures>) {
        distanceMap = mutableMapOf<Int, Int>()
        treasureList.forEach { treasure ->
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getTreasureImage(treasure.idTreasure)

            }
            val coordinates = LatLng(treasure.latitude, treasure.longitude)
            val results = FloatArray(3)

            Location.distanceBetween(
                currentCoordinates.latitude,
                currentCoordinates.longitude,
                treasure.latitude,
                treasure.longitude,
                results
            )

            distanceMap[treasure.idTreasure] = results[0].toInt()

            val treasureMarker = MarkerOptions().position(coordinates).title(treasure.name)
                .snippet("${treasure.idTreasure},${treasure.score},${results[0]}")
            if (results[0] <= 1000) {
                treasureMarker.icon(markerColor("#4E6A55"))
            } else {
                treasureMarker.icon(markerColor("#5A4A37"))
            }
            map.addMarker(treasureMarker)
        }
        viewModel.distanceMapVM.postValue(distanceMap)
    }

    fun markerColor(color: String?): BitmapDescriptor {
        val hsv = FloatArray(3)
        Color.colorToHSV(Color.parseColor(color), hsv)
        return BitmapDescriptorFactory.defaultMarker(hsv[0])
    }

    fun createMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.treasureListData.observe(viewLifecycleOwner) { treasureList ->
            createMarkers(treasureList)
        }
        enableLocation()
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(currentCoordinates, 14f),
            1500, null
        )
        map.setOnInfoWindowClickListener(this)
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

        map.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext(), viewModel))
        route?.remove()
        val start = "${currentCoordinates.longitude},${currentCoordinates.latitude}"
        val end = "${treasureMarker.position.longitude},${treasureMarker.position.latitude}"
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getRoute(
                "5b3ce3597851110001cf624877a97a68b1a84fa2bcb01fb0aa655b89",
                start,
                end
            )
            withContext(Dispatchers.Main) {
                drawRoute()
            }
        }

        return false
    }

    fun drawRoute() {
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


    override fun onInfoWindowClick(marker: Marker) {
        val distance = marker.snippet!!.split(",")[2].toDouble().toInt()
        if (distance <= 1000) {
            requireView().findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToTreasureDetailFragment(
                    marker.snippet!!.split(",")[0].toInt()
                )
            )
        } else {
            Toast.makeText(
                requireContext(),
                "Get closer to the treasure to start the hunt",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


}