package com.example.turingparking.helpers

import android.view.View

interface ParkingListClickListener {
    fun onParkingListItemClick(view: View, id: String)
}