package com.example.turingparking.firebase_classes

class Stops(var userId: String){

    var id: String = ""
    var carId: String = ""
    var parkingId: String = ""
    var preferential: Boolean = false
    var electric: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var cancelled: Boolean = false
    var active: Boolean = false
    var finalized: Boolean = false
    var cost: Double = 0.0
    var timeOfCheckIn: Long = 0
    var timeOfReserve: Long = 0
    var timeOfReserveCancel: Long = 0
    var timeOfLeave: Long = 0
    var estimatedTimeOfArrive: Long = 0
    var rating: Int = 0
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun toString(): String {
        return "Stops(userId='$userId', id='$id', carId='$carId', parkingId='$parkingId', preferential=$preferential, electric=$electric, occupied=$occupied, reserved=$reserved, cancelled=$cancelled, active=$active, finalized=$finalized, cost=$cost, timeOfCheckIn=$timeOfCheckIn, timeOfReserve=$timeOfReserve, timeOfReserveCancel=$timeOfReserveCancel, timeOfLeave=$timeOfLeave, estimatedTimeOfArrive=$estimatedTimeOfArrive, rating=$rating)"
    }

}