package com.example.turingparking.firebase_classes

class Spots(var parkingId: String) {

    var id: String = ""
    var carId: String = ""
    var reserveId: String = ""
    var preferential: Boolean = false
    var electric: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var estimatedTimeOfArrive: Long = 0
    var timeOfCheckIn: Long = 0
    var timeOfReserve: Long = 0
    var timeOfLeave: Long = 0
    var priority: Int = 1

    override fun toString(): String {
        return "Spots(parkingId='$parkingId', id='$id', carId='$carId', reserveId='$reserveId', preferential=$preferential, electric=$electric, occupied=$occupied, reserved=$reserved, estimatedTimeOfArrive=$estimatedTimeOfArrive, timeOfCheckIn=$timeOfCheckIn, timeOfReserve=$timeOfReserve, timeOfLeave=$timeOfLeave, priority=$priority)"
    }
}