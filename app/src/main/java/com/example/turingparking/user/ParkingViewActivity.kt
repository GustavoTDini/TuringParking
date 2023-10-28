package com.example.turingparking.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.bumptech.glide.Glide
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Stops
import com.example.turingparking.helpers.FirebaseHelpers
import com.example.turingparking.helpers.Helpers
import com.example.turingparking.helpers.TuringSharing
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
import java.util.UUID


class ParkingViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var electricCar = false
    private var handicapCar = false
    private var carId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        storage = Firebase.storage
        db = Firebase.firestore

        val turingSharing = TuringSharing(MyApplication.applicationContext())
        carId = turingSharing.getCarId().toString()
        if (carId.isNotEmpty()) {
            getCarDetails()
        }

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        val nameTextView = findViewById<TextView>(R.id.parking_details_name_text_view)
        val addressTextView = findViewById<TextView>(R.id.parking_details_address_text_view)
        val parkingImageView = findViewById<ImageView>(R.id.parking_details_image_view)
        val insuranceView = findViewById<LinearLayout>(R.id.parking_details_insurance_view)
        val favoriteButton = findViewById<ImageButton>(R.id.parking_details_fav_button)
        val ratingsView = findViewById<LinearLayout>(R.id.parking_details_rating_view)
        val ratingsBar = findViewById<RatingBar>(R.id.parking_details_rating_bar)
        val evaluationNumberTextView = findViewById<TextView>(R.id.parking_details_evaluation_text_view)
        val newParkingTextView = findViewById<TextView>(R.id.parking_details_new_parking_text_view)
        val totalSpotsTextView = findViewById<TextView>(R.id.parking_details_total_spots_text_view)
        val handicapSpotsTextView = findViewById<TextView>(R.id.parking_details_handicap_spots_text_view)
        val electricSpotsTextView = findViewById<TextView>(R.id.parking_details_electric_spots_text_view)
        val twentyFourHourTextView = findViewById<TextView>(R.id.parking_details_twenty_four_text_view)
        val openHourView = findViewById<LinearLayout>(R.id.parking_details_open_hour_view)
        val openHourTextView = findViewById<TextView>(R.id.parking_details_open_hour_text_view)
        val closeHourView = findViewById<LinearLayout>(R.id.parking_details_close_hour_view)
        val closeHourTextView = findViewById<TextView>(R.id.parking_details_close_hour_text_view)
        val price15MinView = findViewById<LinearLayout>(R.id.parking_details_15min_view)
        val price15MinTextView = findViewById<TextView>(R.id.parking_details_15min_text_view)
        val price1HourView = findViewById<LinearLayout>(R.id.parking_details_1hour_view)
        val price1HourTextView = findViewById<TextView>(R.id.parking_details_1hour_text_view)
        val price24HourView = findViewById<LinearLayout>(R.id.parking_details_24hour_view)
        val price24HourTextView = findViewById<TextView>(R.id.parking_details_24hour_text_view)
        val priceNightView = findViewById<LinearLayout>(R.id.parking_details_night_view)
        val priceNightTextView = findViewById<TextView>(R.id.parking_details_night_text_view)
        val bookingButton = findViewById<Button>(R.id.parking_details_book_button)

        val parkingId = intent.extras?.getString("id")

        if (parkingId != null) {
            db.collection("parkings").document(parkingId)
                .get()
                .addOnSuccessListener { result ->
                    val parkingData = result.data
                    if(parkingData !== null){
                        val name = parkingData["name"] as String
                        val insured = parkingData["insured"] as Boolean
                        val addressStreet = parkingData["addressStreet"] as String
                        val addressNumber = parkingData["addressNumber"] as String
                        val addressComplement = parkingData["addressComplement"] as String
                        val addressDistrict = parkingData["addressDistrict"] as String
                        val addressCity = parkingData["addressCity"] as String
                        val addressState = parkingData["addressState"] as String
                        val latitude = parkingData["latitude"] as Double
                        val longitude = parkingData["longitude"] as Double
                        val openHour = parkingData["openHour"] as String
                        val closeHour = parkingData["closeHour"] as String
                        val twentyFour = parkingData["twentyFour"] as Boolean
                        val priceFor15 = parkingData["priceFor15"] as Double
                        val priceForHour = parkingData["priceForHour"] as Double
                        val priceFor24Hour = parkingData["priceFor24Hour"] as Double
                        val priceForNight = parkingData["priceForNight"] as Double
                        val spotsLong = parkingData["spots"] as Long
                        val spots = spotsLong.toInt()
                        val electricSpotsLong = parkingData["electricSpots"] as Long
                        val electricSpots = electricSpotsLong.toInt()
                        val handicapSpotsLong = parkingData["handicapSpots"] as Long
                        val handicapSpots = handicapSpotsLong.toInt()
                        val usedSpotsLong = parkingData["usedSpots"] as Long
                        val usedSpots = usedSpotsLong.toInt()
                        val usedElectricSpotsLong = parkingData["usedElectricSpots"] as Long
                        val usedElectricSpots = usedElectricSpotsLong.toInt()
                        val usedHandicapSpotsLong = parkingData["usedHandicapSpots"] as Long
                        val usedHandicapSpots = usedHandicapSpotsLong.toInt()
                        val evaluationsLong = parkingData["evaluations"] as Long
                        val evaluations = evaluationsLong.toInt()
                        val ratingLong = parkingData["rating"] as Number
                        val rating = ratingLong.toDouble()
                        val imageUrl = parkingData["imageUri"] as String

                        if (insured){
                            insuranceView.visibility = View.VISIBLE
                        } else{
                            insuranceView.visibility = View.GONE
                        }

                        nameTextView.text = name

                        val address = "$addressStreet, $addressNumber $addressComplement - $addressDistrict. $addressCity - $addressState"
                        addressTextView.text = address

                        if (evaluations <= 0){
                            ratingsView.visibility = View.GONE
                            newParkingTextView.visibility = View.VISIBLE
                        } else {
                            ratingsView.visibility = View.VISIBLE
                            val evaluationsText = "Baseado em $evaluations avaliações"
                            evaluationNumberTextView.text = evaluationsText
                            ratingsBar.progress = rating.toInt()
                            newParkingTextView.visibility = View.GONE
                        }

                        val availableSpots = spots - usedSpots - usedElectricSpots - usedHandicapSpots
                        val availableElectricSpots = electricSpots - usedElectricSpots
                        val availableHandicapSpots = handicapSpots - usedHandicapSpots

                        val availableSpotsText = "$availableSpots de $spots"
                        val availableElectricSpotsText = "$availableElectricSpots de $electricSpots"
                        val availableHandicapSpotsText = "$availableHandicapSpots de $handicapSpots"

                        totalSpotsTextView.text = availableSpotsText
                        electricSpotsTextView.text = availableElectricSpotsText
                        handicapSpotsTextView.text = availableHandicapSpotsText

                        if(twentyFour){
                            twentyFourHourTextView.visibility = View.VISIBLE
                            openHourView.visibility = View.GONE
                            closeHourView.visibility = View.GONE
                        } else{
                            twentyFourHourTextView.visibility = View.GONE
                            openHourView.visibility = View.VISIBLE
                            val openHourText = "${openHour.take(2)}:${openHour.takeLast(2)}"
                            openHourTextView.text = openHourText
                            closeHourView.visibility = View.VISIBLE
                            val closeHourText = "${closeHour.take(2)}:${closeHour.takeLast(2)}"
                            closeHourTextView.text = closeHourText
                        }

                        if (priceFor15 < 0){
                            price15MinView.visibility = View.GONE
                        } else{
                            price15MinView.visibility = View.VISIBLE
                            price15MinTextView.text = format.format(priceFor15)
                        }

                        if (priceForHour < 0){
                            price1HourView.visibility = View.GONE
                        } else{
                            price1HourView.visibility = View.VISIBLE
                            price1HourTextView.text = format.format(priceForHour)
                        }

                        if (priceFor24Hour < 0){
                            price24HourView.visibility = View.GONE
                        } else{
                            price24HourView.visibility = View.VISIBLE
                            price24HourTextView.text = format.format(priceFor24Hour)
                        }

                        if (priceForNight < 0){
                            priceNightView.visibility = View.GONE
                        } else{
                            priceNightView.visibility = View.VISIBLE
                            priceNightTextView.text = format.format(priceForNight)
                        }

                        val imageRef = storage.getReferenceFromUrl(imageUrl)
                        imageRef.downloadUrl.addOnSuccessListener {uri->
                            val imageURL = uri.toString()
                            Glide.with(this)
                                .load(imageURL)
                                .into(parkingImageView)
                        }

                        FirebaseHelpers.checkIfIsFavorite(currentUser, parkingId, favoriteButton)

                        favoriteButton.setOnClickListener{
                            FirebaseHelpers.setFavorite(favoriteButton, currentUser, parkingId)
                        }

                        bookingButton.setOnClickListener {
                            if (carId.isEmpty()) {
                                Toast.makeText(
                                    this,
                                    "Cadastre um Veículo para estacionar",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val mainIntent = Intent(this, MainUserActivity::class.java)
                                startActivity(mainIntent)
                            } else {
                                val id = UUID.randomUUID().toString()
                                val stop = Stops(currentUser?.uid.toString())
                                stop.active = true
                                stop.parkingId = parkingId
                                stop.id = id
                                stop.carId = carId
                                stop.latitude = latitude
                                stop.longitude = longitude
                                stop.reserved = true
                                stop.timeOfReserve = System.currentTimeMillis()
                                if (electricCar) {
                                    Log.d(TAG, "onCreate: ElectricCar")
                                    if (availableElectricSpots <= 0) {
                                        openElectricCarDialog(stop, name, id, parkingId)
                                    } else {
                                        stop.electric = true
                                        confirmReserve(name, id, stop, parkingId)
                                    }
                                } else if (handicapCar) {
                                    Log.d(TAG, "onCreate: HandicapCar")
                                    if (availableHandicapSpots <= 0) {
                                        openHandicapCarDialog(stop, name, id, parkingId)
                                    } else {
                                        stop.preferential = true
                                        confirmReserve(name, id, stop, parkingId)
                                    }
                                } else {
                                    Log.d(TAG, "onCreate: Plain")
                                    confirmReserve(name, id, stop, parkingId)
                                }
                            }
                        }
                    } else{
                        goToMap()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun openHandicapCarDialog(
        stop: Stops,
        name: String,
        id: String,
        parkingId: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Não há mais vagas preferenciais, podemos colocá-lo em uma vaga comum?")
            .setCancelable(false)
            .setPositiveButton("Sim") { dialog, _ ->
                stop.preferential = false
                dialog.dismiss()
                confirmReserve(name, id, stop, parkingId)
            }
            .setNegativeButton("Não") { dialog, _ ->
                goToMap()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun openElectricCarDialog(
        stop: Stops,
        name: String,
        id: String,
        parkingId: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Não há mais vagas para Carro elétrico, podemos coloca-lo em uma vaga comum?")
            .setCancelable(false)
            .setPositiveButton("Sim") { dialog, _ ->
                stop.electric = false
                dialog.dismiss()
                confirmReserve(name, id, stop, parkingId)
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
                goToMap()
            }
        val alert = builder.create()
        alert.show()
    }





    private fun getCarDetails() {
        db.collection("cars").document(carId).get()
            .addOnSuccessListener { document ->
                val car = document.data
                if (car != null) {
                    electricCar = car["electric"] as Boolean
                    handicapCar = car["handicap"] as Boolean
                }
            }.addOnFailureListener { e ->
                Log.d(TAG, "Falha no requisição Firebase $e")
            }
    }

    private fun confirmReserve(
        name: String,
        id: String,
        stop: Stops,
        parkingId: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Confirma sua reserva no estacionamento $name?")
            .setCancelable(false)
            .setPositiveButton("Sim") { dialog, _ ->
                findSpot(parkingId, stop, stop.electric, stop.preferential)
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                goToMap()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun findSpot(
        parkingId: String,
        stop: Stops,
        electric: Boolean,
        preferential: Boolean
    ) {
        db.collection("spots")
            .whereEqualTo("parkingId", parkingId)
            .whereEqualTo("electric", electric)
            .whereEqualTo("preferential", preferential)
            .limit(1).get().addOnSuccessListener { document ->
                val data = document.documents[0]
                val spotId = data.id
                val occupied = false
                val reserved = true
                stop.spotId = spotId
                val priority = Helpers.definePriority(electric, preferential, occupied, reserved)
                updateReservedSpot(spotId, carId, stop, priority, parkingId)
            }
    }

    private fun updateReservedSpot(
        spotId: String,
        carId: String,
        stop: Stops,
        priority: Int,
        parkingId: String
    ) {
        val db = Firebase.firestore
        db.collection("spots").document(spotId)
            .update("reserved", true, "carId", carId, "timeOfReserve", stop.timeOfReserve, "reserveId", stop.id, "priority", priority)
            .addOnSuccessListener {
                setReserve(stop)
                if (stop.electric) {
                    incrementUsedParkingSpot(parkingId, "usedElectricSpots", this)
                } else if (stop.preferential) {
                    incrementUsedParkingSpot(parkingId, "usedHandicapSpots", this)
                } else {
                    incrementUsedParkingSpot(parkingId, "usedSpots", this)
                }
            }
    }

    private fun setReserve(
        stop: Stops,
    ) {
        val db = Firebase.firestore
        db.collection("stops").document(stop.id).set(stop)
    }

    private fun incrementUsedParkingSpot(parkingId: String, spotType: String, activity: Activity) {
        val db = Firebase.firestore
        db.collection("parkings").document(parkingId)
            .update(
                spotType, FieldValue.increment(1)
            ).addOnSuccessListener {
                NavUtils.navigateUpFromSameTask(activity)
            }
    }

    private fun goToMap(){
        NavUtils.navigateUpFromSameTask(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goToMap()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        private const val TAG = "ParkingViewActivity"
    }


}