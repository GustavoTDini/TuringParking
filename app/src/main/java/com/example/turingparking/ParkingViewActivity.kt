package com.example.turingparking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.data.Parking
import com.example.turingparking.data.Stop
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Currency

class ParkingViewActivity : AppCompatActivity() {
    private var mParking: Parking? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tvTitle = findViewById<TextView>(R.id.titleview)
        val tvPrice = findViewById<TextView>(R.id.priceTxt)
        val tvVacancy = findViewById<TextView>(R.id.vacancyTxt)
        val tvInsurance = findViewById<TextView>(R.id.insuranceInfo)
        val bookButton = findViewById<Button>(R.id.bookBtn)
        val favButton = findViewById<ImageButton>(R.id.favBtn)

        val parkingId = intent.extras?.getString("id")?.toInt()

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        lifecycleScope.launch {
            mParking = parkingId?.let { MyApplication.database?.parkingDao()?.getParkingFromId(it) }
            if (mParking != null) {
                tvTitle.text = mParking!!.parkingName
                tvPrice.text = format.format(mParking!!.preco)
                val vagas_disponiveis = mParking!!.vagas - mParking!!.ocupadas
                tvVacancy.text = buildString {
                    append(vagas_disponiveis.toString())
                    append(" de ")
                    append(mParking!!.vagas)
                }
                if (mParking!!.seguro){
                    tvInsurance.text = "Com Seguro"
                } else{
                    tvInsurance.text = "Sem Seguro"
                }
            }
        }

        bookButton.setOnClickListener{
            lifecycleScope.launch {
                val timestamp = LocalDate.now().toString()
                val preferences = this@ParkingViewActivity.getSharedPreferences("user_preferences", MODE_PRIVATE)
                val userId = preferences.getInt("UserId",-1)
                if (userId == -1){
                    Toast.makeText(this@ParkingViewActivity, "Usuario não Logado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ParkingViewActivity, StartActivity::class.java)
                    startActivity(intent)
                } else{
                    val book = mParking?.parkingId?.let { it1 -> Stop(it1, userId, timestamp ) }
                    if (book != null) {
                        MyApplication.database?.stopDao()?.insert(book)
                        val intent = Intent(this@ParkingViewActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }

        favButton.setOnClickListener{
            Toast.makeText(this@ParkingViewActivity, "Função ainda a ser implementada", Toast.LENGTH_SHORT).show()
        }
    }



}