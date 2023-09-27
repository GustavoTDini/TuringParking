package com.example.turingparking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class StartActivity : AppCompatActivity() {

    private val permissionId = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setPermissions()

        // Funcionalidade para o botão de Login
        val login_btn = findViewById<Button>(R.id.loginButton)
        login_btn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Funcionalidade para o botão do Facebook - ainda a ser implementada
        val facebook_btn = findViewById<Button>(R.id.facebookButton)
        facebook_btn.setOnClickListener{
            Toast.makeText(this@StartActivity, getString(R.string.facebook_toast), Toast.LENGTH_SHORT).show()
        }

        // Funcionalidade para o botão de register
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Funcionalidade para o botão de register
        val permissonButton = findViewById<Button>(R.id.permissonButton)
        permissonButton.setOnClickListener{
            setPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setPermissions() {
        if (!checkPermissions()) {
            if (!isLocationEnabled()) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
}