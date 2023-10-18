package com.example.turingparking.helpers

import android.view.View

interface CarListClickListener {
    fun onCarListItemClick(view: View, id: String)
}