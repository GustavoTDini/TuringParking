package com.example.turingparking.adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turingparking.databinding.ListItemHistoryBinding
import com.example.turingparking.firebase_classes.Stops
import com.example.turingparking.helpers.HistoryListClickListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


class StopsHistoryRecyclerViewAdapter(
    private val values: List<Stops>, private val historyListClickListener: HistoryListClickListener
) : RecyclerView.Adapter<StopsHistoryRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        storage = Firebase.storage
        db = Firebase.firestore
        return ViewHolder(
            ListItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = values[position]
        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance()
        formatCurrency.maximumFractionDigits = 2
        formatCurrency.currency = Currency.getInstance("BRL")
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale("pt-BR"))
        val timeFormat = SimpleDateFormat("hh:mm", Locale("pt-BR"))

        db.collection("parkings").document(spot.parkingId).get().addOnSuccessListener {document->
            val parking = document.data
            if (parking != null){
                val parkingName = parking["name"] as String
                holder.parkingName.text = parkingName
                val parkingUri = parking["imageUri"] as String
                val imageRef = storage.getReferenceFromUrl(parkingUri)
                imageRef.downloadUrl.addOnSuccessListener {uri->
                    val imageURL = uri.toString()
                    Glide.with(context)
                        .load(imageURL)
                        .into(holder.parkingImage)
                }
            }
        }

        holder.dateTimeStatus.text = "Estacionado no dia ${dateFormat.format(spot.timeOfCheckIn)} das ${timeFormat.format(spot.timeOfCheckIn)} as ${timeFormat.format(spot.timeOfLeave)}"

        if (spot.rating < 0){
            holder.ratingView.visibility = View.GONE
            holder.notRated.visibility = View.VISIBLE
        } else{
            holder.ratingView.visibility = View.VISIBLE
            holder.notRated.visibility = View.GONE
            holder.rating.progress = spot.rating.toInt()
        }



        holder.paidPrice.text = formatCurrency.format(spot.cost)

        holder.view.setOnClickListener{
            historyListClickListener.onHistoryListItemClick(it, spot.id)
        }

    }
    
    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val parkingImage: ImageView = binding.historyListImageView
        val parkingName: TextView = binding.historyListParkingNameTextView
        val paidPrice: TextView = binding.historyListPriceTextView
        val dateTimeStatus: TextView = binding.historyListDateTime
        val rating: RatingBar = binding.historyListRatingBar
        val notRated: TextView = binding.historyListNotRated
        val ratingView: LinearLayout = binding.historyListRatingView
        val view: ConstraintLayout = binding.historyListItem

        override fun toString(): String {
            return super.toString() + " '" + parkingName.text + "'" + paidPrice.text
        }

    }

    companion object {
        private const val TAG = "StopsHistoryRecyclerView"
    }
}
