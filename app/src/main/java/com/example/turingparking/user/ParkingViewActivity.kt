package com.example.turingparking.user

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.StartActivity
import com.example.turingparking.data.Parking
import com.example.turingparking.data.Stop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Currency

class ParkingViewActivity : AppCompatActivity() {
    private var mParking: Parking? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        storage = Firebase.storage
        db = Firebase.firestore

        val tvTitle = findViewById<TextView>(R.id.titleview)
        val tvPrice = findViewById<TextView>(R.id.priceTxt)
        val tvVacancy = findViewById<TextView>(R.id.vacancyTxt)
        val tvInsurance = findViewById<TextView>(R.id.insuranceInfo)
        val ivPhoto = findViewById<ImageView>(R.id.parking_image_view)
        val bookButton = findViewById<Button>(R.id.bookBtn)
        val favButton = findViewById<ImageButton>(R.id.favBtn)

        val parkingId = intent.extras?.getString("id")

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        if (parkingId != null) {
            db.collection("parkings").document(parkingId)
                .get()
                .addOnSuccessListener { result ->
                    try{
                        val name = result.getString("name")
                        val price = result.getDouble("priceForHour")
                        val insured = result.getBoolean("insurance")
                        val spots = result.get("spots")
                        val imageUrl = result.getString("imageUri") as String

                        if (insured == true){
                            tvInsurance.text = "Com Seguro"
                        } else{
                            tvInsurance.text = "Sem Seguro"
                        }
                        tvTitle.text = name
                        tvPrice.text = format.format(price)
                        tvVacancy.text = spots.toString()

                        val imageRef = storage.getReferenceFromUrl(imageUrl)
                        imageRef.downloadUrl.addOnSuccessListener {uri->

                            val imageURL = uri.toString()

                            Glide.with(this)
                                .load(imageURL)
                                .into(ivPhoto)

                        }

                    }catch (e: Error){
                        Log.e(TAG, "addMarkers: $e", )
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }

        bookButton.setOnClickListener{
            lifecycleScope.launch {
                val timestamp = LocalDate.now().toString()
                val preferences = this@ParkingViewActivity.getSharedPreferences("user_preferences", MODE_PRIVATE)
                val userId = auth.currentUser?.providerId.toString()
                if (auth.currentUser ==  null){
                    Toast.makeText(this@ParkingViewActivity, "Usuario não Logado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ParkingViewActivity, StartActivity::class.java)
                    startActivity(intent)
                } else{
                    val book = mParking?.parkingId?.let { it1 -> Stop(it1, userId, timestamp ) }
                    if (book != null) {
                        MyApplication.database?.stopDao()?.insert(book)
                        val intent = Intent(this@ParkingViewActivity, MainUserActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }

        favButton.setOnClickListener{
            Toast.makeText(this@ParkingViewActivity, "Função ainda a ser implementada", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        private const val TAG = "ParkingViewActivityLog"
    }


}