package com.example.turingparking.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null
    private var mCurrentPosition: LatLng = LatLng(-23.550244, -46.633908)
    private var mPositionMarker: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        setLocation()
        addMarkers()
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
    }

    override fun onLocationChanged(p0: Location) {
        setLocation()
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {
        mFusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
            val location: Location? = task.result
            if (location != null) {
                mCurrentPosition = LatLng(
                    location.latitude,
                    location.longitude
                )
                mGoogleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        mCurrentPosition, 17f
                    )
                )
                addPositionMark()
            }
        }
    }

    private fun addPositionMark() {
        mPositionMarker?.remove()
        mPositionMarker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(mCurrentPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                .anchor(0.5f, 0.5f)
                .draggable(true)
        )
        mPositionMarker?.hideInfoWindow()
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, 15f))
    }

    private fun addMarkers() {
        lifecycleScope.launch {
            val parkings = MyApplication.database?.parkingDao()?.getAll()
            if (parkings != null) {
                for (park in parkings){
                    val position = LatLng(park.latitude, park.longitude)
                    mGoogleMap?.addMarker(MarkerOptions()
                        .position(position)
                        .title(park.parkingName)
                        .snippet(park.parkingId.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map)))
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