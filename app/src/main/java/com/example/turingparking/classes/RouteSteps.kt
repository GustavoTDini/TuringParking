package com.example.turingparking.classes

class RouteSteps(var startLat: Double, var startLng: Double, var endLat: Double, var endLng: Double ){

    override fun toString(): String {
        return "RouteSteps(startLat=$startLat, startLng=$startLng, endLat=$endLat, endLng=$endLng)"
    }
}