package com.example.turingparking.firebase_classes

class Spots(var parkingId: String) {

    var id: String = ""
    var carId: String = ""
    var reserveId: String = ""
    var preferential: Boolean = false
    var electric: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var timeOfCheckIn: Long = 0
    var timeOfReserve: Long = 0
    var timeOfLeave: Long = 0
    var priority: Int = 1

}