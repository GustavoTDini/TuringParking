package com.example.turingparking.helpers

import android.widget.ImageView
import com.example.turingparking.R

class UIHelpers {

    companion object{

        fun getCarIcon(
            carType: Int,
            carColor: Int,
            imageView: ImageView
        ) {
            if (carType == 0) {
                when (carColor) {
                    0 -> imageView.setImageResource(R.drawable.black_car)
                    1 -> imageView.setImageResource(R.drawable.white_car)
                    2 -> imageView.setImageResource(R.drawable.silver_car)
                    3 -> imageView.setImageResource(R.drawable.grey_car)
                    4 -> imageView.setImageResource(R.drawable.red_car)
                    5 -> imageView.setImageResource(R.drawable.blue_car)
                    6 -> imageView.setImageResource(R.drawable.green_car)
                    7 -> imageView.setImageResource(R.drawable.yellow_car)
                    8 -> imageView.setImageResource(R.drawable.brown_car)
                    9 -> imageView.setImageResource(R.drawable.rainbow_car)
                }
            }

            if (carType == 1) {
                when (carColor) {
                    0 -> imageView.setImageResource(R.drawable.black_suv)
                    1 -> imageView.setImageResource(R.drawable.white_suv)
                    2 -> imageView.setImageResource(R.drawable.silver_suv)
                    3 -> imageView.setImageResource(R.drawable.grey_suv)
                    4 -> imageView.setImageResource(R.drawable.red_suv)
                    5 -> imageView.setImageResource(R.drawable.blue_suv)
                    6 -> imageView.setImageResource(R.drawable.green_suv)
                    7 -> imageView.setImageResource(R.drawable.yellow_suv)
                    8 -> imageView.setImageResource(R.drawable.brown_suv)
                    9 -> imageView.setImageResource(R.drawable.rainbow_suv)
                }
            }

            if (carType == 2) {
                when (carColor) {
                    0 -> imageView.setImageResource(R.drawable.black_pickup)
                    1 -> imageView.setImageResource(R.drawable.white_pickup)
                    2 -> imageView.setImageResource(R.drawable.silver_pickup)
                    3 -> imageView.setImageResource(R.drawable.grey_pickup)
                    4 -> imageView.setImageResource(R.drawable.red_pickup)
                    5 -> imageView.setImageResource(R.drawable.blue_pickup)
                    6 -> imageView.setImageResource(R.drawable.green_pickup)
                    7 -> imageView.setImageResource(R.drawable.yellow_pickup)
                    8 -> imageView.setImageResource(R.drawable.brown_pickup)
                    9 -> imageView.setImageResource(R.drawable.rainbow_pickup)
                }
            }
        }

    }
}