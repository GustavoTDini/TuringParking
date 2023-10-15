package com.example.turingparking.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.databinding.ListItemCarBinding
import com.example.turingparking.firebase_classes.Car

class CarListRecyclerViewAdapter(
    private val values: List<Car>
) : RecyclerView.Adapter<CarListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: $values")
        Log.d(TAG, "onCreateViewHolder: $parent")
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
        if (!car.eletric) {
            holder.eletricImage.visibility = View.GONE
        } else {
            holder.eletricImage.visibility = View.VISIBLE
        }
        if (!car.handicap) {
            holder.handicapImage.visibility = View.GONE
        } else {
            holder.handicapImage.visibility = View.VISIBLE
        }

        if (car.type == 0) {
            when (car.color) {
                0 -> holder.carImage.setImageResource(R.drawable.black_car)
                1 -> holder.carImage.setImageResource(R.drawable.white_car)
                2 -> holder.carImage.setImageResource(R.drawable.silver_car)
                3 -> holder.carImage.setImageResource(R.drawable.grey_car)
                4 -> holder.carImage.setImageResource(R.drawable.red_car)
                5 -> holder.carImage.setImageResource(R.drawable.blue_car)
                6 -> holder.carImage.setImageResource(R.drawable.green_car)
                7 -> holder.carImage.setImageResource(R.drawable.yellow_car)
                8 -> holder.carImage.setImageResource(R.drawable.brown_car)
                9 -> holder.carImage.setImageResource(R.drawable.rainbow_car)
            }
        }

        if (car.type == 1) {
            when (car.color) {
                0 -> holder.carImage.setImageResource(R.drawable.black_suv)
                1 -> holder.carImage.setImageResource(R.drawable.white_suv)
                2 -> holder.carImage.setImageResource(R.drawable.silver_suv)
                3 -> holder.carImage.setImageResource(R.drawable.grey_suv)
                4 -> holder.carImage.setImageResource(R.drawable.red_suv)
                5 -> holder.carImage.setImageResource(R.drawable.blue_suv)
                6 -> holder.carImage.setImageResource(R.drawable.green_suv)
                7 -> holder.carImage.setImageResource(R.drawable.yellow_suv)
                8 -> holder.carImage.setImageResource(R.drawable.brown_suv)
                9 -> holder.carImage.setImageResource(R.drawable.rainbow_suv)
            }
        }

        if (car.type == 2) {
            when (car.color) {
                0 -> holder.carImage.setImageResource(R.drawable.black_pickup)
                1 -> holder.carImage.setImageResource(R.drawable.white_pickup)
                2 -> holder.carImage.setImageResource(R.drawable.silver_pickup)
                3 -> holder.carImage.setImageResource(R.drawable.grey_pickup)
                4 -> holder.carImage.setImageResource(R.drawable.red_pickup)
                5 -> holder.carImage.setImageResource(R.drawable.blue_pickup)
                6 -> holder.carImage.setImageResource(R.drawable.green_pickup)
                7 -> holder.carImage.setImageResource(R.drawable.yellow_pickup)
                8 -> holder.carImage.setImageResource(R.drawable.brown_pickup)
                9 -> holder.carImage.setImageResource(R.drawable.rainbow_pickup)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val nick: TextView = binding.nickRecyclerItemTextView
        val plate: TextView = binding.plateRecyclerItemTextView
        val carImage: ImageView = binding.carIconImageView
        val eletricImage: ImageView = binding.eletricIconImageView
        val handicapImage: ImageView = binding.handicapIconImageView

        override fun toString(): String {
            return super.toString() + " '" + nick.text + "'"
        }

    }

    companion object {
        private const val TAG = "CarListRecyclerViewAdapter"
    }

}