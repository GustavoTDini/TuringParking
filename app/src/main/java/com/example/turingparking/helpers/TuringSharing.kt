package com.example.turingparking.helpers

import android.content.Context
import android.content.SharedPreferences

class TuringSharing(context: Context) {

    private val CURRENT_CAR = "currentCarId"
    private val REFRESH_ESTIMATED_TIME = "estimatedTimeRefresh"
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

    fun setEstimatedTimeClock(nextRefresh: Long) {
        val editor = sharedPrefs!!.edit()
        editor.putLong(REFRESH_ESTIMATED_TIME, nextRefresh)
        editor.apply()
    }

    fun getRefreshTime(): Long {
        return sharedPrefs!!.getLong(REFRESH_ESTIMATED_TIME, -1)
    }

}