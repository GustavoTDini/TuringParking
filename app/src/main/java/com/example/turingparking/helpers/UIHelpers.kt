package com.example.turingparking.helpers

import com.example.turingparking.R

class UIHelpers {

    companion object{

        val avatarArray = listOf<Int>(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            R.drawable.avatar_16,
            R.drawable.avatar_17,
            R.drawable.avatar_18,
            R.drawable.avatar_19,
            R.drawable.avatar_20,
            R.drawable.avatar_21,
            R.drawable.avatar_22,
            R.drawable.avatar_23,
            R.drawable.avatar_24,
            R.drawable.avatar_25,
            R.drawable.avatar_26,
            R.drawable.avatar_27,
            R.drawable.avatar_28,
            R.drawable.avatar_29,
            R.drawable.avatar_30,
            R.drawable.avatar_31,
            R.drawable.avatar_32,
            R.drawable.avatar_33,
            R.drawable.avatar_34,
            R.drawable.avatar_35,
            R.drawable.avatar_36,
            R.drawable.avatar_37,
            R.drawable.avatar_38,
            R.drawable.avatar_39,
            R.drawable.avatar_40,
            R.drawable.avatar_41,
            R.drawable.avatar_42,
            R.drawable.avatar_43,
            R.drawable.avatar_44,
            R.drawable.avatar_45,
            R.drawable.avatar_46,
            R.drawable.avatar_47,
            R.drawable.avatar_48,
            R.drawable.avatar_49,
            R.drawable.avatar_50)

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