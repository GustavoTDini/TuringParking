package com.example.turingparking.owner_fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Parking
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Currency

class AddFinishFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: AddViewModel by activityViewModels()
    private lateinit var parking: Parking
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private var mGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parking = viewModel.getParking()
        storage = Firebase.storage
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_add_finish, container, false)

        val nameTextView = fragmentView.findViewById<TextView>(R.id.verify_name)
        nameTextView.text = parking.name

        val cnpjTextView = fragmentView.findViewById<TextView>(R.id.verify_cnpj)
        cnpjTextView.text = parking.cnpj

        val spotsTextView = fragmentView.findViewById<TextView>(R.id.verify_spots)
        spotsTextView.text = parking.spots.toString()

        val insuranceTextView = fragmentView.findViewById<TextView>(R.id.verify_insurance)
        if (parking.insured){
            insuranceTextView.text = "Com Seguro"
        } else{
            insuranceTextView.text = "Sem Seguro"
        }

        val cepTextView = fragmentView.findViewById<TextView>(R.id.verify_cep)
        cepTextView.text = parking.cep

        val addressTextView = fragmentView.findViewById<TextView>(R.id.verify_address)
        addressTextView.text = parking.addressStreet

        val numberTextView = fragmentView.findViewById<TextView>(R.id.verify_address_number)
        numberTextView.text = parking.addressNumber

        val complementTextView = fragmentView.findViewById<TextView>(R.id.verify_address_more)
        complementTextView.text = parking.addressComplement

        val districtTextView = fragmentView.findViewById<TextView>(R.id.verify_address_district)
        districtTextView.text = parking.addressDistrict

        val cityTextView = fragmentView.findViewById<TextView>(R.id.verify_address_city)
        cityTextView.text = parking.addressCity

        val stateTextView = fragmentView.findViewById<TextView>(R.id.verify_address_state)
        stateTextView.text = parking.addressState

        val fotoImageView = fragmentView.findViewById<ImageView>(R.id.verify_image)
        fotoImageView.setImageBitmap(viewModel.getImage())

        val view24HoursTime = fragmentView.findViewById<View>(R.id.verify_24_hours_time_view)
        val twentyFourTextView = fragmentView.findViewById<TextView>(R.id.verify_24_hours_time)
        val viewOpenHours = fragmentView.findViewById<View>(R.id.verify_open_time_view)
        val openHourTextView = fragmentView.findViewById<TextView>(R.id.verify_open_time)
        val viewCloseHours = fragmentView.findViewById<View>(R.id.verify_close_time_view)
        val closeHourTextView = fragmentView.findViewById<TextView>(R.id.verify_close_time)

        if (viewModel.is24hours()){
            viewOpenHours.visibility = View.GONE
            viewCloseHours.visibility = View.GONE
            view24HoursTime.visibility = View.VISIBLE
            twentyFourTextView.text = "Sim"
        } else{
            viewOpenHours.visibility = View.VISIBLE
            viewCloseHours.visibility = View.VISIBLE
            view24HoursTime.visibility = View.GONE
            val openHour = "${parking.openHour.take(2)}:${parking.openHour.takeLast(2)}hrs"
            openHourTextView.text = openHour
            val closeHour = "${parking.closeHour.take(2)}:${parking.closeHour.takeLast(2)}hrs"
            closeHourTextView.text = closeHour
        }

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        val view15Min = fragmentView.findViewById<View>(R.id.verify_15_min_view)
        val price15MinTextView = fragmentView.findViewById<TextView>(R.id.verify_15_min)
        val view1Hour = fragmentView.findViewById<View>(R.id.verify_1_hour_view)
        val price1HourTextView = fragmentView.findViewById<TextView>(R.id.verify_1_hour)
        val view24Hours = fragmentView.findViewById<View>(R.id.verify_24_hours_view)
        val price24HoursTextView = fragmentView.findViewById<TextView>(R.id.verify_24_hours)
        val viewNight = fragmentView.findViewById<View>(R.id.verify_night_view)
        val priceNightTextView = fragmentView.findViewById<TextView>(R.id.verify_night)

        if (parking.priceFor15 >= 0){
            view15Min.visibility = View.VISIBLE
            price15MinTextView.text = format.format(parking.priceFor15)
        }

        if(parking.priceForHour >= 0){
            view1Hour.visibility = View.VISIBLE
            price1HourTextView.text = format.format(parking.priceForHour)
        }

        if(parking.priceFor24Hour >= 0){
            view24Hours.visibility = View.VISIBLE
            price24HoursTextView.text = format.format(parking.priceFor24Hour)
        }

        if(parking.priceForNight >= 0){
            viewNight.visibility = View.VISIBLE
            priceNightTextView.text = format.format(parking.priceForNight)
        }

        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
            saveImage(viewModel.getImage(), fragmentView)
        }


        return fragmentView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.verify_check_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        setLocation()
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {

        val position  = viewModel.getCoordinates()
        mGoogleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                position, 15f
            )
        )
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title(viewModel.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map))
                .draggable(false)
        )
    }
    companion object {
        private const val TAG = "AddFinishFragment"
    }

    fun createImageFile(bitmapImage: Bitmap): ByteArray{
        val bos = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.PNG,0, bos)
        return bos.toByteArray()
    }

    private fun saveImage(bitmapImage: Bitmap, view: View) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("parkingImages/${parking.id}.jpg")
        val uploadTask = imageRef.putBytes(createImageFile(bitmapImage))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {it
                    Log.e(TAG, "saveImage: $it")
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                parking.imageUri = downloadUri.toString()
                saveParkingToFirebase(parking, view)
            } else {
                Log.e(TAG, "saveImage: $task")
            }
        }
    }

    private fun saveParkingToFirebase(parking: Parking, view: View) {
        db.collection("parkings").document(parking.id)
            .set(parking)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                view.findNavController().navigate(R.id.nav_about)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}