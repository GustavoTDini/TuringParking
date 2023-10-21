package com.example.turingparking.helpers

import android.content.Context
import android.content.SharedPreferences

class TuringSharing(context: Context) {

    private val CURRENT_CAR = "currentCarId"
    private val TURING_SHARED = "Turing Sharing"

    private var sharedPrefs: SharedPreferences? = context.applicationContext.getSharedPreferences(TURING_SHARED, Context.MODE_PRIVATE)

    @Synchronized
    fun setCarId(carId: String?) {
        val editor = sharedPrefs!!.edit()
        editor.putString(CURRENT_CAR, carId)
        editor.apply()
    }

    fun getCarId(): String? {
        return sharedPrefs!!.getString(CURRENT_CAR, "Empty")
    }

}