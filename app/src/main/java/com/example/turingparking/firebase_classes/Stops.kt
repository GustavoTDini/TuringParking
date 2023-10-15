package com.example.turingparking.firebase_classes

class Stops(var spotId: String){

    var id: String = ""
    var carId: String = ""
    var parkingId: String = ""
    var preferential: Boolean = false
    var electric: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var cost: Double = 0.0
    var timeOfCheckIn: Long = 0
    var timeOfReserve: Long = 0
    var timeOfReserveCancel: Long = 0
    var timeOfLeave: Long = 0

    override fun toString(): String {
        return "Stops(spotId='$spotId', id='$id', carId='$carId', parkingId='$parkingId', preferential=$preferential, electric=$electric, occupied=$occupied, reserved=$reserved, cost=$cost, timeOfCheckIn=$timeOfCheckIn, timeOfReserve=$timeOfReserve, timeOfReserveCancel=$timeOfReserveCancel, timeOfLeave=$timeOfLeave)"
    }
}