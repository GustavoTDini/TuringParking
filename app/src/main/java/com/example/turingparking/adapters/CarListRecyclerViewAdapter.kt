package com.example.turingparking.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.databinding.ListItemCarBinding
import com.example.turingparking.firebase_classes.Car
import com.example.turingparking.helpers.CarListClickListener
import com.example.turingparking.helpers.UIHelpers

class CarListRecyclerViewAdapter(
    private val values: List<Car>, private val carListClickInterface: CarListClickListener, private val currentCarId: String
) : RecyclerView.Adapter<CarListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = values[position]
        Log.d(TAG, "onBindViewHolder: $car")
        holder.nick.text = car.nick
        holder.plate.text = car.plate
        if (!car.electric) {
            holder.electricImage.visibility = View.GONE
        } else {
            holder.electricImage.visibility = View.VISIBLE
        }
        if (!car.handicap) {
            holder.handicapImage.visibility = View.GONE
        } else {
            holder.handicapImage.visibility = View.VISIBLE
        }

        holder.checkCar.isChecked = car.id == currentCarId

        holder.carImage.setImageResource(UIHelpers.getCarIcon(car.type, car.color))

        holder.view.setOnClickListener{
            carListClickInterface.onCarListItemClick(it, car.id)
        }
    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val nick: TextView = binding.nickRecyclerItemTextView
        val plate: TextView = binding.plateRecyclerItemTextView
        val carImage: ImageView = binding.carIconImageView
        val electricImage: ImageView = binding.electricIconImageView
        val handicapImage: ImageView = binding.handicapIconImageView
        val checkCar: CheckBox = binding.checkedCar
        val view: ConstraintLayout = binding.carListItem

        override fun toString(): String {
            return super.toString() + " '" + nick.text + "'"
        }

    }

    companion object {
        private const val TAG = "CarListRecyclerViewAdapter"
    }

}