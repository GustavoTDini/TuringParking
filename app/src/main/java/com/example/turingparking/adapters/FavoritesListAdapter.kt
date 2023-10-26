package com.example.turingparking.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turingparking.databinding.ListItemFavoriteBinding
import com.example.turingparking.helpers.FirebaseHelpers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FavoritesListAdapter
(private val values: List<*>)
: RecyclerView.Adapter<FavoritesListAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore
        return ViewHolder(
            ListItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parkingId = values[position].toString()
        Log.d(TAG, "onBindViewHolder: $parkingId")

        db.collection("parkings").document(parkingId).get().addOnSuccessListener { document->
            val parkingData = document.data
            if (parkingData != null){
                val parkingName = parkingData["name"] as String
                val addressStreet = parkingData["addressStreet"] as String
                val addressNumber = parkingData["addressNumber"] as String
                val addressComplement = parkingData["addressComplement"] as String
                val addressDistrict = parkingData["addressDistrict"] as String
                val addressCity = parkingData["addressCity"] as String
                val addressState = parkingData["addressState"] as String
                val fullAddress = "$addressStreet, $addressNumber $addressComplement - $addressDistrict. $addressCity - $addressState"
                val parkingImageUri = parkingData["imageUri"] as String

                holder.name.text = parkingName
                holder.address.text = fullAddress
                val imageRef = storage.getReferenceFromUrl(parkingImageUri)
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageURL = uri.toString()
                    Glide.with(context)
                        .load(imageURL)
                        .into(holder.image)
                }

                val currentUser = auth.currentUser

                FirebaseHelpers.checkIfIsFavorite(currentUser, parkingId, holder.favButton)

                holder.favButton.setOnClickListener{
                    FirebaseHelpers.setFavorite(holder.favButton, currentUser, parkingId)
                }

            }



        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image: ImageView = binding.favoriteListParkingImageView
        val name: TextView = binding.favoriteListNameTextView
        val address: TextView = binding.favoriteListAddressTextView
        val favButton: ImageButton = binding.favoriteListFavButton

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }

    companion object {
        private const val TAG = "ParkingListRecyclerView"
    }
}