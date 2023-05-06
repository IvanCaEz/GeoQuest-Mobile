package com.example.geoquest_app.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.geoquest_app.R
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.example.geoquest_app.databinding.FragmentMapBinding
import com.example.geoquest_app.viewmodel.GeoViewModel
import com.example.models.Treasures
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    lateinit var binding: FragmentMapBinding
    lateinit var map: GoogleMap
    val viewModel: GeoViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentCoordinates: LatLng
    var route: Polyline? = null



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

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

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


    }

    private fun createMarkers(treasureList: List<Treasures>) {
        treasureList.forEach { treasure ->
            val coordinates = LatLng(treasure.latitude, treasure.longitude)
            val treasureMarker = MarkerOptions().position(coordinates).title(treasure.name)

            map.addMarker(treasureMarker)
        }

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
        val itb = LatLng(41.413182, 2.227171)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(itb, 14f),
            1000, null)



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
        // Borra la ruta anterior
        route?.remove()
        route = null
        val start =  "${currentCoordinates.longitude},${currentCoordinates.latitude}"
        val end = "${treasureMarker.position.longitude},${treasureMarker.position.latitude}"
        viewModel.getRoute("5b3ce3597851110001cf624877a97a68b1a84fa2bcb01fb0aa655b89", start, end)
        drawRoute()
        return false
    }

    fun drawRoute(){
        val polyLineOptions = PolylineOptions()
        // Personalización de la ruta
        /*
        .width(10f)
        .color(ContextCompat.getColor(requireContext(), R.color.color1)?
        .startCap(RoundCap() o ButtCap() o SquareCap o CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.loquesea))
         .endCap() igual
         .pattern(listOf((Dot(), Gap(10f), Dash(50f), Gap(10f)))
         */

        viewModel.route.observe(viewLifecycleOwner){ routeResponse ->
            // mapear coordenadas?
            // devuelve las coordenadas alreves
            routeResponse.features.first().geometry.coordinates.forEach {
                polyLineOptions.add(LatLng(it[1], it[0]))
            }
             route = map.addPolyline(polyLineOptions)
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }



}