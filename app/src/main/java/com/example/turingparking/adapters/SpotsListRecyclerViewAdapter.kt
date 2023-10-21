package com.example.turingparking.adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.databinding.ListItemSpotBinding
import com.example.turingparking.firebase_classes.Car
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.helpers.Helpers.Companion.createCar
import com.example.turingparking.helpers.SpotListClickListener
import com.example.turingparking.helpers.UIHelpers
import com.example.turingparking.user_fragments.CarListFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale


class SpotsListRecyclerViewAdapter(
    private val values: List<Spots>, private val spotListClickInterface: SpotListClickListener
) : RecyclerView.Adapter<SpotsListRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var db: FirebaseFirestore
    private lateinit var car: Car

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

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = values[position]

        if (spot.electric){
            holder.electric.visibility = View.VISIBLE
            holder.handicap.visibility = View.GONE
        } else if (spot.preferential) {
            holder.handicap.visibility = View.VISIBLE
            holder.electric.visibility = View.GONE
        } else {
            holder.handicap.visibility = View.GONE
            holder.electric.visibility = View.GONE
        }

        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale("pt-BR"))
        val timeFormat = SimpleDateFormat("hh:mm:ss", Locale("pt-BR"))

        if (spot.reserved){
            holder.light.setImageResource(R.drawable.yellow_light)
            val status = "Reservado no dia ${dateFormat.format(spot.timeOfReserve)} as ${timeFormat.format(spot.timeOfReserve)}"
            holder.status.text = status
        } else if (spot.occupied){
            holder.light.setImageResource(R.drawable.red_light)
            val status = "Ocupado as ${timeFormat.format(spot.timeOfCheckIn)} do dia ${dateFormat.format(spot.timeOfCheckIn)}"
            holder.status.text = status
        } else{
            holder.light.setImageResource(R.drawable.green_light)
            holder.status.text = "Vaga Disponível"
            holder.carView.visibility = View.GONE
        }

        if (spot.occupied || spot.reserved) {
            holder.carView.visibility = View.VISIBLE
            db.collection("cars").document(spot.carId).get()
                .addOnSuccessListener { document ->
                    val carData = document.data
                    if (carData != null) {
                        val userId = carData["userId"] as String
                        val car = createCar(carData, userId)
                        holder.car.setImageResource(UIHelpers.getCarIcon(car.type, car.color))
                        holder.carPlate.text = car.plate
                        holder.item.setOnClickListener {
                            spotListClickInterface.onSpotListItemClick(it, spot, car)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
                }
        } else{
            holder.item.setOnClickListener {
                spotListClickInterface.onSpotListItemClick(it, spot, null)
            }
        }
    }


    class ViewHolder(
        binding: ListItemSpotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val item: ConstraintLayout = binding.spotListItem
        val light: ImageView = binding.trafficLightSpotImageView
        val status: TextView = binding.spotStatusTextView
        val carView: LinearLayout = binding.spotsItemCarView
        val car: ImageView = binding.spotsCarIconImageView
        val carPlate: TextView = binding.spotStatusPlateTextView
        val electric: ImageView = binding.spotsElectricIconImageView
        val handicap: ImageView = binding.spotsHandicapIconImageView

    }

    companion object {
        private const val TAG = "SpotsListRecyclerViewAd"
    }

}
