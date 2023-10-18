package com.example.turingparking.helpers

import com.example.turingparking.R

class UIHelpers {

    companion object{

        fun getCarIcon(
            carType: Int,
            carColor: Int
        ): Int {
            if (carType == 0) {
                when (carColor) {
                    0 -> return R.drawable.black_car
                    1 -> return R.drawable.white_car
                    2 -> return R.drawable.silver_car
                    3 -> return R.drawable.grey_car
                    4 -> return R.drawable.red_car
                    5 -> return R.drawable.blue_car
                    6 -> return R.drawable.green_car
                    7 -> return R.drawable.yellow_car
                    8 -> return R.drawable.brown_car
                    9 -> return R.drawable.rainbow_car
                }
            }

            if (carType == 1) {
                when (carColor) {
                    0 -> return R.drawable.black_suv
                    1 -> return R.drawable.white_suv
                    2 -> return R.drawable.silver_suv
                    3 -> return R.drawable.grey_suv
                    4 -> return R.drawable.red_suv
                    5 -> return R.drawable.blue_suv
                    6 -> return R.drawable.green_suv
                    7 -> return R.drawable.yellow_suv
                    8 -> return R.drawable.brown_suv
                    9 -> return R.drawable.rainbow_suv
                }
            }

            if (carType == 2) {
                when (carColor) {
                    0 -> return R.drawable.black_pickup
                    1 -> return R.drawable.white_pickup
                    2 -> return R.drawable.silver_pickup
                    3 -> return R.drawable.grey_pickup
                    4 -> return R.drawable.red_pickup
                    5 -> return R.drawable.blue_pickup
                    6 -> return R.drawable.green_pickup
                    7 -> return R.drawable.yellow_pickup
                    8 -> return R.drawable.brown_pickup
                    9 -> return R.drawable.rainbow_pickup
                }
            }
            return R.drawable.turing_car
        }

    }
}