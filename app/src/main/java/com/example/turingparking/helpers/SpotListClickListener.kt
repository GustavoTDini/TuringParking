package com.example.turingparking.helpers

import android.view.View

interface SpotListClickListener {
    fun onParkingListItemClick(view: View, id: String)
}