package com.example.turingparking.user

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Stops
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        storage = Firebase.storage
        db = Firebase.firestore

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

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        if (parkingId != null) {
            db.collection("parkings").document(parkingId)
                .get()
                .addOnSuccessListener { result ->
                    try{
                        val parkingid = result.getString("id") as String
                        val name = result.getString("name") as String
                        val insured = result.getBoolean("insured") as Boolean
                        val addressStreet = result.getString("addressStreet") as String
                        val addressNumber = result.getString("addressNumber") as String
                        val addressComplement = result.getString("addressComplement") as String
                        val addressDistrict = result.getString("addressDistrict") as String
                        val addressCity = result.getString("addressCity") as String
                        val addressState = result.getString("addressState") as String
                        val latitude = result.getDouble("latitude") as Double
                        val longitude = result.getDouble("longitude") as Double
                        val openHour = result.getString("openHour") as String
                        val closeHour = result.getString("closeHour") as String
                        val twentyFour = result.getBoolean("twentyFour") as Boolean
                        val priceFor15 = result.getDouble("priceFor15") as Double
                        val priceForHour = result.getDouble("priceForHour") as Double
                        val priceFor24Hour = result.getDouble("priceFor24Hour") as Double
                        val priceForNight = result.getDouble("priceForNight") as Double
                        val spotsDouble = result.getDouble("spots") as Double
                        val spots = spotsDouble.toInt()
                        val electricSpotsDouble = result.getDouble("electricSpots") as Double
                        val electricSpots = electricSpotsDouble.toInt()
                        val handicapSpotsDouble = result.getDouble("handicapSpots") as Double
                        val handicapSpots = handicapSpotsDouble.toInt()
                        val usedSpotsDouble = result.getDouble("usedSpots") as Double
                        val usedSpots = usedSpotsDouble.toInt()
                        val usedElectricSpotsDouble = result.getDouble("usedElectricSpots") as Double
                        val usedElectricSpots = usedElectricSpotsDouble.toInt()
                        val usedHandicapSpotsDouble = result.getDouble("usedHandicapSpots") as Double
                        val usedHandicapSpots = usedHandicapSpotsDouble.toInt()
                        val evaluationsDouble = result.getDouble("evaluations") as Double
                        val evaluations = evaluationsDouble.toInt()
                        val ratingDouble = result.getDouble("rating") as Double
                        val rating = ratingDouble.toInt()
                        val imageUrl = result.getString("imageUri") as String

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

                        bookingButton.setOnClickListener{
                            val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
                            val carId = sharedPref?.getString("SELECTED_CAR", "Empty").toString()
                            db.collection("cars").document(carId).get()
                                .addOnSuccessListener {document ->
                                    try {
                                        electricCar = document.getBoolean("electric") == true
                                        handicapCar = document.getBoolean("handicap") == true
                                    }catch (e: Error){
                                        Log.e(TAG, "onCreate: $e", )
                                    }
                                }.addOnFailureListener{e->
                                    Log.d(TAG, "Falha no requisição Firebase $e")
                                }

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
                            //TODO: Testar se há vagas respectivas disponiveis
                            if (electricCar){
                                stop.electric = true
                            }
                            if (handicapCar){
                                stop.electric = true
                            }


                            db.collection("stops").document(id).set(stop).addOnSuccessListener {it->
                                if (electricCar){
                                    db.collection("parkings").document(parkingId)
                                        .update("usedSpots", usedSpots + 1,
                                            "usedElectricSpots", usedElectricSpots + 1)
                                } else if(handicapCar){
                                    db.collection("parkings").document(parkingId)
                                        .update("usedSpots", usedSpots + 1,
                                            "usedHandicapSpots", usedHandicapSpots + 1)
                                } else{
                                    db.collection("parkings").document(parkingId)
                                        .update("usedSpots", usedSpots + 1)
                                }
                            }.addOnFailureListener { e->
                                Log.d(TAG, "Falha no requisição Firebase $e")
                            }

//            lifecycleScope.launch {
//                val timestamp = LocalDate.now().toString()
//                val preferences = this@ParkingViewActivity.getSharedPreferences("user_preferences", MODE_PRIVATE)
//                val userId = auth.currentUser?.providerId.toString()
//                if (auth.currentUser ==  null){
//                    Toast.makeText(this@ParkingViewActivity, "Usuario não Logado", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@ParkingViewActivity, StartActivity::class.java)
//                    startActivity(intent)
//                } else{
//                    val book = mParking?.parkingId?.let { it1 -> Stop(it1, userId, timestamp ) }
//                    if (book != null) {
//                        MyApplication.database?.stopDao()?.insert(book)
//                        val intent = Intent(this@ParkingViewActivity, MainUserActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//
//            }
                        }

                        favoriteButton.setOnClickListener{
                            Toast.makeText(this@ParkingViewActivity, "Função ainda a ser implementada", Toast.LENGTH_SHORT).show()
                        }






                    }catch (e: Error){
                        Log.e(TAG, "onCreate: $e", )
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }


        }


    }
    companion object {
        private const val TAG = "ParkingViewActivityLog"
    }


}