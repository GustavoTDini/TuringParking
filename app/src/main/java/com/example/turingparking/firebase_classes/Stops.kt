package com.example.turingparking.firebase_classes

import java.sql.Timestamp

class Stops(var spotId: String){

    var id: String = ""
    var carId: String = ""
    var parkingId: String = ""
    var preferential: Boolean = false
    var eletric: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var cost: Double = 0.0
    var timeOfCheckIn: Timestamp? = null
    var timeOfReserve: Timestamp? = null
    var timeOfReserveCancel: Timestamp? = null
    var timeOfLeave: Timestamp? = null

    override fun toString(): String {
        return "Stops(spotId='$spotId', id='$id', carId='$carId', parkingId='$parkingId', preferential=$preferential, eletric=$eletric, occupied=$occupied, reserved=$reserved, cost=$cost, timeOfCheckIn=$timeOfCheckIn, timeOfReserve=$timeOfReserve, timeOfReserveCancel=$timeOfReserveCancel, timeOfLeave=$timeOfLeave)"
    }
}