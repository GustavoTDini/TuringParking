package com.example.turingparking.user_fragments

import android.annotation.SuppressLint
import android.content.Context
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.turingparking.BuildConfig
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.helpers.TuringSharing
import com.example.turingparking.helpers.UIHelpers.Companion.getCarIcon
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject


class MapsFragment : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null
    private var mCurrentPosition: LatLng = LatLng(-23.550244, -46.633908)
    private var mPositionMarker: Marker? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentCarType = 100
    private var currentCarColor = 100
    private var reserveId = ""
    private var spotId = ""
    private var parkingId = ""
    private var reserved = false
    private var parked = false
    private var routeList: ArrayList<LatLng> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        setLocation()
        db.collection("stops")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .whereEqualTo("active", true)
            .addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (document != null) {
                    if (document.documents.size > 0){
                        val currentReserve = document.documents[0]
                        val latitudeDest = currentReserve["latitude"] as Double
                        val longitudeDest = currentReserve["longitude"] as Double
                        reserved = currentReserve["reserved"] as Boolean
                        parked = currentReserve["occupied"] as Boolean
                        reserveId = currentReserve["id"] as String
                        spotId = currentReserve["spotId"] as String
                        parkingId = currentReserve["parkingId"] as String
                        getRouteWithDirections(latitudeDest, longitudeDest, mCurrentPosition.latitude, mCurrentPosition.longitude, "driving", requireContext())
                        addMarkerToSelectedParking()
                        if (reserved){
                            addPositionMark(getCarIcon(currentCarType, currentCarColor))
                        } else{
                            addPositionMark(R.drawable.avatar_1)
                            addCarMarkToParking(getCarIcon(currentCarType, currentCarColor), LatLng(latitudeDest, longitudeDest))
                        }
                    } else{
                        addMarkers()
                        addPositionMark(getCarIcon(currentCarType, currentCarColor))
                    }
                }
            }
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
            }
        }
    }

    private fun addPositionMark(drawable: Int) {
        mPositionMarker?.remove()
        val height = 200
        val width = 200
        val bitmapDraw = ResourcesCompat.getDrawable(resources,drawable, null) as BitmapDrawable
        val bitmap = bitmapDraw.bitmap
        val marker = Bitmap.createScaledBitmap(bitmap, width, height, false)
        mPositionMarker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(mCurrentPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(marker))
                .anchor(0.5f, 0.5f)
                .draggable(true)
        )
        mPositionMarker?.hideInfoWindow()
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPosition, 15f))
    }

    private fun addCarMarkToParking(drawable: Int, position: LatLng) {
        mPositionMarker?.remove()
        val height = 200
        val width = 200
        val bitmapDraw = ResourcesCompat.getDrawable(resources,drawable, null) as BitmapDrawable
        val bitmap = bitmapDraw.bitmap
        val marker = Bitmap.createScaledBitmap(bitmap, width, height, false)
        mPositionMarker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(marker))
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

    private fun addMarkerToSelectedParking() {
        db.collection("parkings").document(parkingId)
            .get()
            .addOnSuccessListener { result ->
                val parking = result.data
                if (parking != null){
                    val latitude = parking["latitude"] as Double
                    val longitude = parking["longitude"] as Double
                    val name = parking["name"] as String
                    val id = parking["id"] as String
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

    fun getRouteWithDirections(
        latitudeDest: Double,
        longitudeDest: Double,
        latitudeOrig: Double,
        longitudeOrig: Double,
        mode: String,
        context: Context
    ) {
        val baseUrl = "https://maps.googleapis.com/maps/api/directions/json"
        val destinationParam = "destination=$latitudeDest,$longitudeDest"
        val originParam = "origin=$latitudeOrig,$longitudeOrig"
        val modeParam = "mode=$mode"
        val keyParam = "key=${BuildConfig.MAPS_API_KEY}"
        val url = "$baseUrl?$destinationParam&$originParam&$modeParam&$keyParam"
        // creating a new variable for our request queue
        val queue = Volley.newRequestQueue(context)
        // making a string request on below line.
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject?> { json->
                val routesArray = json["routes"] as JSONArray
                val routeJson = routesArray[0] as JSONObject
                val legsArray = routeJson["legs"] as JSONArray
                val legsJson = legsArray[0] as JSONObject
                val duration = legsJson["duration"] as JSONObject
                val durationMili = duration["value"] as Int

                val stepsArray = legsJson["steps"] as JSONArray
                routeList.add(LatLng(latitudeOrig, longitudeOrig))
                for (i in 0 until stepsArray.length()) {
                    val step = stepsArray.getJSONObject(i)
                    val startLocation = step["start_location"] as JSONObject
                    val startLat = startLocation["lat"] as Double
                    val startLng = startLocation["lng"] as Double
                    val routeStep = LatLng ( startLat, startLng )
                    routeList.add(routeStep)
                }
                routeList.add(LatLng(latitudeDest, longitudeDest))
                val routePolyline = PolylineOptions().color(resources.getColor(R.color.secondary_container)).width(15f).addAll(routeList)
                mGoogleMap?.addPolyline(routePolyline)

                val estimatedTime = System.currentTimeMillis() + durationMili*1000
                //FirebaseHelpers.updateEstimatedArrivalTime(reserveId, spotId, estimatedTime)

                Log.d("Response", duration.toString())
            }
        ) { e-> Log.d("Error.Response", e.toString()) }
        queue.add(request)
    }

    companion object {
        private const val TAG = "MapsFragment"
    }
}