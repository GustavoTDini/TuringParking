package com.example.turingparking.user_fragments

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.turingparking.R
import com.example.turingparking.helpers.FirebaseHelpers
import com.example.turingparking.helpers.UIHelpers
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class StopHistoryDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var mGoogleMap: GoogleMap? = null
    private var parkingPosition: LatLng = LatLng(-23.550244, -46.633908)
    private var parkingName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val stopId = arguments?.getString("stopId") as String
        val currentUser = auth.currentUser
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale("pt-BR"))
        val timeFormat = SimpleDateFormat("hh:mm", Locale("pt-BR"))
        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance()
        formatCurrency.maximumFractionDigits = 2
        formatCurrency.currency = Currency.getInstance("BRL")

        val fragmentView =  inflater.inflate(R.layout.fragment_stop_history_detail, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.detail_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this )
        val parkingNameTextView = fragmentView.findViewById<TextView>(R.id.stop_details_name_text_view)
        val parkingAddressTextView = fragmentView.findViewById<TextView>(R.id.stop_details_address_text_view)
        val parkingImageView = fragmentView.findViewById<ImageView>(R.id.stop_details_image_view)
        val insuredView = fragmentView.findViewById<LinearLayout>(R.id.stop_details_insurance_view)
        val favButton = fragmentView.findViewById<ImageButton>(R.id.stop_details_fav_button)
        val evaluateTextView = fragmentView.findViewById<TextView>(R.id.stop_detail_evaluate)
        val thankTextView = fragmentView.findViewById<TextView>(R.id.stop_detail_thank)
        val stopRatingBar = fragmentView.findViewById<RatingBar>(R.id.stop_details_rating_bar)
        val stopEvaluateButton = fragmentView.findViewById<Button>(R.id.stop_details_evaluate_button)
        val stopSummaryTextView = fragmentView.findViewById<TextView>(R.id.stop_details_summary)
        val stopCarElectricIcon = fragmentView.findViewById<ImageView>(R.id.stop_details_electric_icon_image_view)
        val stopCarHandicapIcon = fragmentView.findViewById<ImageView>(R.id.stop_details_handicap_icon_image_view)
        val stopCarImageView = fragmentView.findViewById<ImageView>(R.id.stop_details_car_icon_image_view)
        val stopCarNickTextView = fragmentView.findViewById<TextView>(R.id.stop_details_car_nick_text_view)
        val backButton = fragmentView.findViewById<Button>(R.id.stop_details_back_button)
        val invoiceButton = fragmentView.findViewById<Button>(R.id.stop_details_invoice_button)
        Log.d(TAG, "onCreateView: $stopId")

        db.collection("stops").document(stopId).get().addOnSuccessListener {document->
            val stop = document.data
            if (stop != null){
                val stopRating = stop["rating"] as Number
                val parkingLat = stop["latitude"] as Double
                val parkingLng = stop["longitude"] as Double
                parkingPosition = LatLng(parkingLat, parkingLng)
                val carId = stop["carId"] as String
                val parkingId = stop["parkingId"] as String
                val reserveTime = stop["timeOfReserve"] as Long
                val checkInTime = stop["timeOfCheckIn"] as Long
                val leaveTime = stop["timeOfLeave"] as Long
                val cost = stop["cost"] as Double

                if (stopRating.toInt() < 0){
                    evaluateTextView.visibility = View.VISIBLE
                    stopRatingBar.setIsIndicator(false)
                    stopRatingBar.progress = 0
                    stopEvaluateButton.visibility = View.VISIBLE
                    thankTextView.visibility = View.GONE
                } else{
                    evaluateTextView.visibility = View.GONE
                    stopRatingBar.setIsIndicator(true)
                    stopRatingBar.progress = stopRating.toInt()
                    stopEvaluateButton.visibility = View.GONE
                    thankTextView.visibility = View.VISIBLE
                }
                db.collection("cars").document(carId).get().addOnSuccessListener { document->
                    val car = document.data
                    if (car != null){
                        val nick = car["nick"] as String
                        val type = (car["type"] as Long).toInt()
                        val color = (car["color"] as Long).toInt()
                        val electric = car["electric"] as Boolean
                        val handicap = car["handicap"] as Boolean
                        val plate = car["plate"] as String
                        if (electric){
                            stopCarElectricIcon.visibility = View.VISIBLE
                        } else{
                            stopCarElectricIcon.visibility = View.GONE
                        }
                        if (handicap){
                            stopCarHandicapIcon.visibility = View.VISIBLE
                        } else{
                            stopCarHandicapIcon.visibility = View.GONE
                        }
                        stopCarImageView.setImageResource(UIHelpers.getCarIcon(type, color))
                        stopCarNickTextView.text = nick

                        db.collection("parkings").document(parkingId).get().addOnSuccessListener {document->
                            val parking = document.data
                            if (parking != null){
                                val parkingName = parking["name"] as String
                                val insured = parking["insured"] as Boolean
                                val addressStreet = parking["addressStreet"] as String
                                val addressNumber = parking["addressNumber"] as String
                                val addressComplement = parking["addressComplement"] as String
                                val addressDistrict = parking["addressDistrict"] as String
                                val addressCity = parking["addressCity"] as String
                                val addressState = parking["addressState"] as String
                                val imageUrl = parking["imageUri"] as String
                                val parkingRating = parking["rating"] as Number
                                val parkingEvaluations = parking["evaluations"] as Number
                                parkingNameTextView.text = parkingName
                                val address = "$addressStreet, $addressNumber $addressComplement - $addressDistrict. $addressCity - $addressState"
                                parkingAddressTextView.text = address
                                if (insured){
                                    insuredView.visibility = View.VISIBLE
                                } else{
                                    insuredView.visibility = View.GONE
                                }

                                val imageRef = storage.getReferenceFromUrl(imageUrl)
                                imageRef.downloadUrl.addOnSuccessListener {uri->
                                    val imageURL = uri.toString()
                                    Glide.with(this)
                                        .load(imageURL)
                                        .into(parkingImageView)
                                }
                                val detailsText = "No dia ${dateFormat.format(checkInTime)}, você estacionou o seu carro $nick, no estacionamento $parkingName, reservando esta vaga as ${timeFormat.format(reserveTime)}, estacionando as ${timeFormat.format(checkInTime)} e saindo as ${timeFormat.format(leaveTime)}, gastando nessa parada o valor de ${formatCurrency.format(cost)}"
                                stopSummaryTextView.text = detailsText

                                stopEvaluateButton.setOnClickListener {
                                    val builder = AlertDialog.Builder(requireContext())
                                    builder.setMessage("Confirma essa Nota para este estacionamento?")
                                        .setCancelable(false)
                                        .setPositiveButton("Sim") { dialog, _ ->
                                            evaluateTextView.visibility = View.GONE
                                            stopRatingBar.setIsIndicator(true)
                                            stopEvaluateButton.visibility = View.GONE
                                            thankTextView.visibility = View.VISIBLE
                                            Log.d(TAG, "onCreateView: ${stopRatingBar.rating}")
                                            Log.d(TAG, "onCreateView: ${stopRatingBar.progress}")
                                            db.collection("stops").document(stopId).update("rating", stopRatingBar.progress).addOnSuccessListener {
                                                val newParkingRating = (parkingRating.toDouble()*parkingEvaluations.toDouble() + stopRatingBar.progress)/(parkingEvaluations.toDouble()+1)

                                                db.collection("parkings").document(parkingId).update("rating", newParkingRating, "evaluations", FieldValue.increment(1)).addOnSuccessListener {
                                                    dialog.dismiss()
                                                }
                                            }
                                        }
                                        .setNegativeButton("Não") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                    val alert = builder.create()
                                    alert.show()
                                }

                                FirebaseHelpers.checkIfIsFavorite(currentUser, parkingId, favButton)

                                favButton.setOnClickListener{
                                    FirebaseHelpers.setFavorite(favButton, currentUser, parkingId)
                                }


                            }

                        }


                    }

                }

            }

        }.addOnFailureListener { e->
            Log.e(TAG, "onCreateView: $e")
        }

        backButton.setOnClickListener{
            Toast.makeText(requireContext(), "Função ainda não implementada!", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener{
            findNavController().popBackStack()
        }



        return fragmentView
    }

    companion object {
        private const val TAG = "StopHistoryDetailFragment"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        setLocation()
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {
        mGoogleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                parkingPosition, 17f
            )
        )
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mGoogleMap
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(parkingPosition)
                .title(parkingName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map))
                .draggable(false)
        )
    }
}