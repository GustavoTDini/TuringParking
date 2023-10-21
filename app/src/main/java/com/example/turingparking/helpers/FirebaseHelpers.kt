package com.example.turingparking.helpers

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.navigation.findNavController
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.classes.ParkingList
import com.example.turingparking.firebase_classes.Car
import com.example.turingparking.firebase_classes.Parking
import com.example.turingparking.firebase_classes.Pix
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.helpers.Helpers.Companion.definePriority
import com.example.turingparking.user_fragments.AddCarFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class FirebaseHelpers {

    companion object{

        private const val TAG = "FirebaseHelpers"
        fun saveParkingToFirebase(parking: Parking, view: View) {
            val db = Firebase.firestore
            db.collection("parkings").document(parking.id)
                .set(parking)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    view.findNavController().navigate(R.id.nav_about)
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        fun saveImage(bitmapImage: Bitmap, view: View, parking: Parking) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("parkingImages/${parking.id}.jpg")
            val uploadTask = imageRef.putBytes(Helpers.createImageFile(bitmapImage))
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

        fun unfavoritePark(
            userId: String,
            parkingId: String,
            favoriteButton: ImageButton,
        ) {
            val db = Firebase.firestore
            db.collection("users").document(userId)
                .update("favorites", FieldValue.arrayRemove(parkingId)).addOnSuccessListener {
                    favoriteButton.setImageResource(R.drawable.empty_star)
                    favoriteButton.tag = "empty"
                }
        }

        fun favoritePark(
            userId: String,
            parkingId: String,
            favoriteButton: ImageButton,
        ) {
            val db = Firebase.firestore
            db.collection("users").document(userId)
                .update("favorites", FieldValue.arrayUnion(parkingId)).addOnSuccessListener {
                    favoriteButton.setImageResource(R.drawable.full_star)
                    favoriteButton.tag = "full"
                }
        }

        fun saveCar(
            id: String,
            car: Car,
            view: View
        ) {
            val db = Firebase.firestore
            db.collection("cars").document(id)
                .set(car)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    val turingSharing = TuringSharing(MyApplication.applicationContext())
                    turingSharing.setCarId(id)
                    view.findNavController().navigate(R.id.nav_cars_list)
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        fun addPIx(
            pix: Pix,
            fragmentView: View,
            context: Context
        ) {
            val db = Firebase.firestore
            db.collection("pix").document(pix.id).set(pix)
                .addOnSuccessListener {
                    incrementWallet(pix.walletId, pix.value, fragmentView)
                    Log.d(AddCarFragment.TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Falha no envio do PIX!", Toast.LENGTH_SHORT)
                        .show()
                    Log.w(AddCarFragment.TAG, "Error writing document", e)
                }
        }

        private fun incrementWallet(
            walletId: String,
            value: Double,
            fragmentView: View
        ) {
            val db = Firebase.firestore
            db.collection("wallets").document(walletId).update(
                "currentValue", FieldValue.increment(value)
            ).addOnSuccessListener {
                fragmentView.findNavController().navigate(R.id.nav_wallet)
            }
        }

        fun spendWallet(
            userId: String,
            value: Double
        ) {
            val db = Firebase.firestore
            db.collection("wallets").whereEqualTo("userId", userId).get().addOnSuccessListener { documents->
                val walletId = documents.documents[0].id
                db.collection("wallets").document(walletId).update(
                    "currentValue", FieldValue.increment(-value)
                )
            }
        }

        fun updateReservedSpot(
            spotId: String,
            carId: String,
            reserveTime: Long,
            stopId: String,
            priority: Int
        ) {
            val db = Firebase.firestore
            db.collection("spots").document(spotId)
                .update("reserved", true, "carId", carId, "timeOfReserve", reserveTime, "reserveId", stopId, "priority", priority)
        }

        fun updateCheckInSpot(
            spotId: String,
            checkInTime: Long,
            priority: Int
        ) {
            val db = Firebase.firestore
            db.collection("spots").document(spotId)
                .update("reserved", false, "occupied", true, "timeOfCheckIn", checkInTime, "priority", priority)
        }

        fun updateCheckInStop(
            stopId: String,
            checkInTime: Long,
        ) {
            val db = Firebase.firestore
            db.collection("stops").document(stopId)
                .update("reserved", false, "occupied", true, "timeOfCheckIn", checkInTime)
        }

        fun updateLeaveSpot(
            spotId: String,
            leaveTime: Long,
            priority: Int
        ) {
            val db = Firebase.firestore
            db.collection("spots").document(spotId)
                .update("occupied", false, "carId", "", "reserveId", "" , "timeOfLeave", leaveTime, "priority", priority)
        }

        fun updateLeaveStop(
            stopId: String,
            leaveTime: Long,
            cost: Double
        ) {
            val db = Firebase.firestore
            db.collection("stops").document(stopId)
                .update("occupied", false, "active", false, "finalized", true, "timeOfLeave", leaveTime, "cost", cost)
        }

        fun incrementUsedParkingSpot(parkingId: String, spotType: String, activity: Activity) {
            val db = Firebase.firestore
            db.collection("parkings").document(parkingId)
                .update(
                    spotType, FieldValue.increment(1)
                ).addOnSuccessListener {
                    NavUtils.navigateUpFromSameTask(activity)
                }
        }

        fun decrementUsedParkingSpot(parkingId: String, spotType: String) {
            val db = Firebase.firestore
            db.collection("parkings").document(parkingId)
                .update(
                    spotType, FieldValue.increment(-1)
                )
        }

        fun createParkingSpots(
            parking: ParkingList,
            handicapSpots: Int,
            electricSpots: Int
        ) {
            val db = Firebase.firestore
            db.collection("spots").whereEqualTo("parkingId", parking.id)
                .get().addOnSuccessListener { documents ->
                    if (documents.size() < parking.spots) {
                        val toAdd = parking.spots - documents.size() - handicapSpots - electricSpots
                        for (i in 1..handicapSpots) {
                            val spot = Spots(parking.id)
                            val id = UUID.randomUUID().toString()
                            spot.id = id
                            spot.preferential = true
                            spot.priority = definePriority(spot.electric, spot.preferential, spot.occupied, spot.reserved)
                            db.collection("spots").document(id).set(spot)
                        }
                        for (i in 1..electricSpots) {
                            val spot = Spots(parking.id)
                            val id = UUID.randomUUID().toString()
                            spot.id = id
                            spot.electric = true
                            spot.priority = definePriority(spot.electric, spot.preferential, spot.occupied, spot.reserved)
                            db.collection("spots").document(id).set(spot)
                        }
                        for (i in 1..toAdd) {
                            val spot = Spots(parking.id)
                            val id = UUID.randomUUID().toString()
                            spot.id = id
                            spot.priority = definePriority(spot.electric, spot.preferential, spot.occupied, spot.reserved)
                            db.collection("spots").document(id).set(spot)
                        }
                    }
                }
        }





    }



}