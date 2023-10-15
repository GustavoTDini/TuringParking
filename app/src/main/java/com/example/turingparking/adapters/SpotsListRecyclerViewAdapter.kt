package com.example.turingparking.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.databinding.ListItemSpotBinding
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.helpers.UIHelpers
import com.example.turingparking.user_fragments.CarListFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


class SpotsListRecyclerViewAdapter(
    private val values: List<Spots>
) : RecyclerView.Adapter<SpotsListRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var db: FirebaseFirestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        db = Firebase.firestore
        return ViewHolder(
            ListItemSpotBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = values[position]
        val formatDate: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatTime: DateTimeFormatter? = DateTimeFormatter.ofPattern("HH:mm")

        if (spot.electric){
            holder.electric.visibility = View.VISIBLE
        }

        if (spot.preferential){
            holder.handicap.visibility = View.VISIBLE
        }

        if (spot.reserved){
            holder.light.setImageResource(R.drawable.yellow_light)
            val date = Date(spot.timeOfReserve).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val status = "Reservado no dia  ${formatDate?.format(date)} as ${formatTime?.format(date)}"
            holder.status.text = status
        } else if (spot.occupied){
            holder.light.setImageResource(R.drawable.red_light)
            val date = Date(spot.timeOfReserve).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val status = "Ocupado as ${formatTime?.format(date)} do dia ${formatDate?.format(date)}"
            holder.status.text = status
        } else{
            holder.light.setImageResource(R.drawable.green_light)
            holder.status.text = "Vaga Disponivel"
        }

        if (spot.occupied || spot.reserved) {
            db.collection("cars").document(spot.carId).get()
                .addOnSuccessListener { document ->
                    val carData = document.data
                    if (carData != null) {
                        val type = carData["type"] as Long
                        val typeInt = type.toInt()
                        val color = carData["color"] as Long
                        val colorInt = color.toInt()
                        UIHelpers.getCarIcon(typeInt, colorInt, holder.car)
                        holder.car.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
                }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemSpotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val light: ImageView = binding.trafficLightSpotImageView
        val status: TextView = binding.spotStatusTextView
        val car: ImageView = binding.spotsCarIconImageView
        val electric: ImageView = binding.spotsElectricIconImageView
        val handicap: ImageView = binding.spotsHandicapIconImageView
    }

    companion object {
        private const val TAG = "TransactionListRecyclerViewAdapter"
    }

}