package com.example.turingparking.user_fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.helpers.TuringSharing
import com.example.turingparking.helpers.UIHelpers
import com.example.turingparking.user.ParkingViewActivity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapsFragment : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null
    private var mCurrentPosition: LatLng = LatLng(-23.550244, -46.633908)
    private var mPositionMarker: Marker? = null
    private lateinit var db: FirebaseFirestore
    private var currentCarType = 100
    private var currentCarColor = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

    }

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
        val turingSharing = TuringSharing(MyApplication.applicationContext())
        val carId = turingSharing.getCarId().toString()
        db.collection("cars").document(carId)
            .addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (document != null) {
                    if (document.data != null) {
                        val carType = document.data!!["type"] as Long
                        val carColor = document.data!!["color"] as Long
                        currentCarColor = carColor.toInt()
                        currentCarType = carType.toInt()
                    }
                }
            }


//        {document ->
//                if (document.data !== null){
//                    val carType = document.data!!["type"] as Long
//                    val carColor = document.data!!["color"] as Long
//                    currentCarColor = carColor.toInt()
//                    currentCarType = carType.toInt()
//                }
//            }.addOnFailureListener{e->
//                currentCarType = 100
//                currentCarColor = 100
//                Log.d(TAG, "Falha no requisição Firebase $e")
//            }

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
        val height = 200
        val width = 200
        val bitmapCarDraw = ResourcesCompat.getDrawable(resources,UIHelpers.getCarIcon(currentCarType, currentCarColor), null) as BitmapDrawable
        val bitmapCar = bitmapCarDraw.bitmap
        val smallCarMarker = Bitmap.createScaledBitmap(bitmapCar, width, height, false)
        mPositionMarker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(mCurrentPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(smallCarMarker))
                .anchor(0.5f, 0.5f)
                .draggable(true)
        )
        mPositionMarker?.hideInfoWindow()
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, 15f))
    }

    private fun addMarkers() {

        db.collection("parkings")
            .get()
            .addOnSuccessListener { result ->
                for (parking in result) {
                    val latitude = parking.getDouble("latitude") as Double
                    val longitude = parking.getDouble("longitude") as Double
                    val name = parking.getString("name")
                    val id = parking.getString("id")
                    try{
                        LatLng(latitude, longitude).let {
                            MarkerOptions()
                                .position(it)
                                .title(name)
                                .snippet(id)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map))
                        }.let { mGoogleMap?.addMarker(it)}  
                    }catch (e: Error){
                        Log.e(TAG, "addMarkers: $e")
                    } 
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
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

    companion object {
        private const val TAG = "MapsFragment"
    }
}