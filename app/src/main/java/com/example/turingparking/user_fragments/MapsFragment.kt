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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.turingparking.BuildConfig
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.helpers.FirebaseHelpers
import com.example.turingparking.helpers.TuringSharing
import com.example.turingparking.helpers.UIHelpers
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject


class MapsFragment : Fragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private var markers: ArrayList<Marker> = ArrayList()
    private var currentPosition: LatLng = LatLng(-23.550244, -46.633908)
    private lateinit var cancelFab: ExtendedFloatingActionButton
    private var carPositionMarker: Marker? = null
    private var walkPositionMarker: Marker? = null
    private var routePolyLine: Polyline? = null
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
    private lateinit var reserveListener: ListenerRegistration
    private lateinit var carListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        setLocation()
    }

    override fun onStop() {
        super.onStop()
        val turingSharing = TuringSharing(MyApplication.applicationContext())
        val carId = turingSharing.getCarId().toString()
        if (carId.isNotEmpty()) {
            carListener.remove()
        }
        reserveListener.remove()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        reserveListener = db.collection("stops")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .whereEqualTo("active", true)
            .addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (document != null) {
                    setLocation()
                    if (document.documents.size > 0){
                        val currentReserve = document.documents[0]
                        val latitudeDest = currentReserve["latitude"] as Double
                        val longitudeDest = currentReserve["longitude"] as Double
                        reserved = currentReserve["reserved"] as Boolean
                        parked = currentReserve["occupied"] as Boolean
                        reserveId = currentReserve["id"] as String
                        spotId = currentReserve["spotId"] as String
                        parkingId = currentReserve["parkingId"] as String
                        if (this.googleMap !== null){
                            addMarkerToSelectedParking()
                            if (reserved) {
                                getRouteWithDirections(
                                    latitudeDest,
                                    longitudeDest,
                                    currentPosition.latitude,
                                    currentPosition.longitude,
                                    "driving",
                                    requireContext()
                                )
                                addCarPositionMark(
                                    getCarIcon(currentCarType, currentCarColor),
                                    currentPosition
                                )
                                cancelFab.visibility = View.VISIBLE

                            } else if (parked) {
                                getRouteWithDirections(
                                    latitudeDest,
                                    longitudeDest,
                                    currentPosition.latitude,
                                    currentPosition.longitude,
                                    "walking",
                                    requireContext()
                                )
                                cancelFab.visibility = View.GONE
                                addCarPositionMark(
                                    getCarIcon(currentCarType, currentCarColor),
                                    LatLng(latitudeDest, longitudeDest)
                                )
                                addWalkPositionMark(currentPosition)
                            }
                        }
                    }else{
                        if (this.googleMap != null){
                            cancelFab.visibility = View.GONE
                            addMarkers()
                            addCarPositionMark(getCarIcon(currentCarType, currentCarColor), currentPosition)
                        }

                    }
                }
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_maps, container, false)
        cancelFab = fragmentView.findViewById(R.id.map_view_cancel_fab) as ExtendedFloatingActionButton
        cancelFab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Cancelar a Reserva Atual?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, _ ->
                    FirebaseHelpers.cancelReserve(reserveId, spotId)
                    routePolyLine?.remove()
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        val turingSharing = TuringSharing(MyApplication.applicationContext())
        val carId = turingSharing.getCarId().toString()
        if (carId.isNotEmpty()) {
            carListener = db.collection("cars").document(carId)
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
        }


        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.main_map) as SupportMapFragment
        mapFragment.getMapAsync(this )
    }

    override fun onLocationChanged(p0: Location) {
        setLocation()
        if(parked){
            addWalkPositionMark(currentPosition)
        } else{
            addCarPositionMark(getCarIcon(currentCarType, currentCarColor), currentPosition)
        }
    }

    override fun onResume() {
        super.onResume()
        if (googleMap != null){
            setLocation()
            if(parked){
                addWalkPositionMark(currentPosition)
            } else{
                addCarPositionMark(getCarIcon(currentCarType, currentCarColor), currentPosition)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
            val location: Location? = task.result
            if (location != null) {
                currentPosition = LatLng(
                    location.latitude,
                    location.longitude
                )
            }
        }
    }

    private fun moveToLocation() {
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentPosition, 15f
            )
        )
    }

    private fun addCarPositionMark(drawable: Int, position: LatLng) {
        carPositionMarker?.remove()
        val height = 200
        val width = 200
        val bitmapDraw = ResourcesCompat.getDrawable(resources,drawable, null) as BitmapDrawable
        val bitmap = bitmapDraw.bitmap
        val marker = Bitmap.createScaledBitmap(bitmap, width, height, false)
        carPositionMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(marker))
                .anchor(0.5f, 0.5f)
                .draggable(true)
        )
        carPositionMarker?.hideInfoWindow()
        moveToLocation()
    }

    private fun addWalkPositionMark(position: LatLng) {
        val userId = auth.currentUser?.uid.toString()
        var avatar = 0
        walkPositionMarker?.remove()
        db.collection("users").document(userId).get().addOnSuccessListener {
            avatar = (it.data?.get("avatar") as Number).toInt()
            val avatarResource = UIHelpers.avatarArray[avatar]
            val height = 150
            val width = 150
            val bitmapDraw = ResourcesCompat.getDrawable(resources,avatarResource, null) as BitmapDrawable
            val bitmap = bitmapDraw.bitmap
            val marker = Bitmap.createScaledBitmap(bitmap, width, height, false)
            walkPositionMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
                    .anchor(0.5f, 0.5f)
                    .draggable(true)
            )
            walkPositionMarker?.hideInfoWindow()
        }
    }

    private fun addMarkers() {
        removeMarkers()
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
                        }.let {
                            val marker = googleMap?.addMarker(it)
                            if (marker != null) {
                                markers.add(marker)
                            }

                        }
                    } catch (e: Error) {
                        Log.e(TAG, "addMarkers: $e")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun removeMarkers() {
        for (marker in markers) {
            marker.remove()
        }
        markers.clear()
    }

    private fun addMarkerToSelectedParking() {
        removeMarkers()
        db.collection("parkings").document(parkingId)
            .get()
            .addOnSuccessListener { result ->
                val parking = result.data
                if (parking != null) {
                    val latitude = parking["latitude"] as Double
                    val longitude = parking["longitude"] as Double
                    val name = parking["name"] as String
                    val id = parking["id"] as String
                    try{
                        LatLng(latitude, longitude).let {
                            MarkerOptions()
                                .position(it)
                                .title(name)
                                .snippet(null)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map))
                        }.let {
                            val marker = googleMap?.addMarker(it)
                            if (marker != null) {
                                markers.add(marker)
                            }
                        }
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
        return if (marker.snippet !== null) {
            if (!reserved || !parked) {
                val parkingViewIntent = Intent(context, ParkingViewActivity::class.java)
                parkingViewIntent.putExtra("id", marker.snippet)
                startActivity(parkingViewIntent)
            }
            true
        } else{
            false
        }
    }

    private fun getRouteWithDirections(
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
                val durationSec = duration["value"] as Int

                val stepsArray = legsJson["steps"] as JSONArray
                createRouteFromArray(latitudeOrig, longitudeOrig, stepsArray, latitudeDest, longitudeDest)

                if (reserved){
                    val estimatedTime = System.currentTimeMillis() + durationSec*1000
                    val turingSharing = TuringSharing(MyApplication.applicationContext())
                    val refreshTime = turingSharing.getRefreshTime()
                    if (refreshTime != null) {
                        if (refreshTime < 0){
                            val nextRefreshTime = System.currentTimeMillis() + 60000
                            turingSharing.setEstimatedTimeClock(nextRefreshTime)
                            FirebaseHelpers.updateEstimatedArrivalTime(reserveId, spotId, estimatedTime)
                        }
                    }
                    if (System.currentTimeMillis() > refreshTime){
                        FirebaseHelpers.updateEstimatedArrivalTime(reserveId, spotId, estimatedTime)
                        val nextRefreshTime = System.currentTimeMillis() + 60000
                        turingSharing.setEstimatedTimeClock(nextRefreshTime)
                    }
                }
            }
        ) { e-> Log.d("Error.Response", e.toString()) }
        queue.add(request)
    }

    private fun createRouteFromArray(
        latitudeOrig: Double,
        longitudeOrig: Double,
        stepsArray: JSONArray,
        latitudeDest: Double,
        longitudeDest: Double
    ) {
        routePolyLine?.remove()
        routeList.clear()
        routeList.add(LatLng(latitudeOrig, longitudeOrig))
        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            val startLocation = step["start_location"] as JSONObject
            val startLat = startLocation["lat"] as Double
            val startLng = startLocation["lng"] as Double
            val routeStep = LatLng(startLat, startLng)
            routeList.add(routeStep)
        }
        routeList.add(LatLng(latitudeDest, longitudeDest))
        val routePolyLineOptions = context?.resources?.let {
            PolylineOptions().color(it.getColor(R.color.secondary_container)).width(20f)
                .addAll(routeList)
                .endCap(RoundCap())
                .startCap(RoundCap())
        }

        routePolyLine = routePolyLineOptions?.let { googleMap?.addPolyline(it) }
        }

    companion object {
        private const val TAG = "MapsFragment"
    }
}