package com.example.turingparking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class StartActivity : AppCompatActivity() {

    private val loginActivity = 1010
    private val registerActivity = 1011
    private val none = 1100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        checkPermission(none)

        // Funcionalidade para o botão de Login
        val login_btn = findViewById<Button>(R.id.login_button)
        login_btn.setOnClickListener{
            checkPermission(loginActivity)
        }

        // Funcionalidade para o botão do Google - ainda a ser implementada
        val facebook_btn = findViewById<Button>(R.id.google_button)
        facebook_btn.setOnClickListener{
            Toast.makeText(this@StartActivity, getString(R.string.facebook_toast), Toast.LENGTH_SHORT).show()
        }

        // Funcionalidade para o botão de register
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener{
            checkPermission(registerActivity)
        }

    }

    private fun checkPermission(intentCode: Int) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                switchActivitiesIntents(intentCode)
            } else {
                Toast.makeText(this, "Por favor, ligue o serviço de localização", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions(intentCode)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions(intentCode: Int) {
        locationPermissionRequest(intentCode).launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
        )
    }

    private fun locationPermissionRequest(intentCode: Int): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    switchActivitiesIntents(intentCode)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    switchActivitiesIntents(intentCode)
                } else -> {
                Toast.makeText(this, "Para o funcionamento do App, é necessário utilizar o serviço de localização!", Toast.LENGTH_LONG).show()
            }
            }
        }
    }

    private fun switchActivitiesIntents(intent: Int){
        when(intent){
            loginActivity ->{
                val nextActivity = Intent(this, LoginActivity::class.java)
                startActivity(nextActivity)
            }
            registerActivity ->{
                val nextActivity = Intent(this, RegisterActivity::class.java)
                startActivity(nextActivity)
            }
            none -> return
        }

    }
}