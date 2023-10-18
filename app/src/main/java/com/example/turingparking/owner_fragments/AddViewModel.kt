package com.example.turingparking.owner_fragments

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.turingparking.firebase_classes.Parking
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddViewModel : ViewModel() {

    private lateinit var newParking: Parking
    private lateinit var auth: FirebaseAuth
    private lateinit var image: Bitmap

    fun intanceNewParking() {
        auth = Firebase.auth
        val id = UUID.randomUUID()
        newParking = Parking(auth.currentUser?.uid.toString())
        newParking.id = id.toString()
    }

    fun addNameAndCnpj(name: String, cnpj: String) {
        newParking.name = name
        newParking.cnpj = cnpj
    }

    fun addAddress(cep: String, street: String, number: String, complement: String, district: String, state: String, city: String) {
        newParking.cep = cep
        newParking.addressStreet = street
        newParking.addressNumber = number
        newParking.addressComplement = complement
        newParking.addressDistrict = district
        newParking.addressState = state
        newParking.addressCity = city
    }

    fun addCoordinates(latitude: Double, longitude: Double) {
        newParking.latitude = latitude
        newParking.longitude = longitude
    }

    fun addHours(openHour: String, closeHour: String, twentyFour: Boolean) {
        newParking.openHour = openHour
        newParking.closeHour = closeHour
        newParking.twentyFour = twentyFour
    }

    fun addFinance(priceFor15: Double, priceForHour: Double, priceFor24Hour: Double, priceForNight: Double, insured: Boolean, spots: Int, electricSpots: Int, handicapSpots: Int) {
        newParking.priceFor15 = priceFor15
        newParking.priceForHour = priceForHour
        newParking.priceFor24Hour = priceFor24Hour
        newParking.priceForNight = priceForNight
        newParking.insured = insured
        newParking.spots = spots
        newParking.electricSpots = electricSpots
        newParking.handicapSpots = handicapSpots
    }

    fun getCoordinates(): LatLng {
        return LatLng(newParking.latitude, newParking.longitude)
    }

    fun getName(): String{
        return newParking.name
    }

    fun printParking(): String {
        return newParking.toString()
    }

    fun is24hours(): Boolean {
        return newParking.twentyFour
    }

    fun getImage(): Bitmap {
        return image
    }

    fun saveImage(bitmap: Bitmap){
        image = bitmap
    }

    fun getParking(): Parking {
        return this.newParking
    }
}