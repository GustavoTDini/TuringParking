package com.example.turingparking.helpers

import android.view.View
import com.example.turingparking.firebase_classes.Car
import com.example.turingparking.firebase_classes.Spots

interface SpotListClickListener {
    fun onSpotListItemClick(
        view: View,
        spot: Spots,
        car: Car?
    )
}