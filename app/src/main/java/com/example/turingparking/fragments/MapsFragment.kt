package com.example.turingparking.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.MyApplication
import com.example.turingparking.ParkingViewActivity
import com.example.turingparking.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    private val permissionId = 10011001
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null
    private var mCurrentPosition: LatLng = LatLng(-23.550244, -46.633908)
    private var mPositionMarker: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener(this)
        getCurrentPositionAddMark(googleMap)
        addMarkers(googleMap)
        mGoogleMap = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this )
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        mCurrentPosition = LatLng(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(this.context, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this.requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this.requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this.requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    override fun onLocationChanged(p0: Location) {
        getCurrentPositionAddMark(mGoogleMap)
    }

    private fun getCurrentPositionAddMark(googleMap: GoogleMap?) {
        mPositionMarker?.remove()
        getLocation()
        mPositionMarker = googleMap?.addMarker(MarkerOptions().position(mCurrentPosition)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)))
        mPositionMarker?.hideInfoWindow()
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, 15f))
    }

    private fun addMarkers(googleMap: GoogleMap?) {
        lifecycleScope.launch {
            val parkings = MyApplication.database?.parkingDao()?.getAll()
            if (parkings != null) {
                for (park in parkings){
                    val position = LatLng(park.latitude, park.longitude)
                    googleMap?.addMarker(MarkerOptions()
                        .position(position)
                        .title(park.parkingName)
                        .snippet(park.parkingId.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)))
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return if (marker.snippet !== null){
            val parkingViewIntent = Intent(context, ParkingViewActivity::class.java)
            parkingViewIntent.putExtra("id", marker.snippet)
            startActivity(parkingViewIntent)
            true
        } else{
            false
        }
    }
}