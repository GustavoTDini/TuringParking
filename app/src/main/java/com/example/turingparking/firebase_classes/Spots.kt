package com.example.turingparking.firebase_classes

import java.sql.Timestamp

class Spots(var parkingId: String) {

    var id: String = ""
    var carId: String = ""
    var preferential: Boolean = false
    var occupied: Boolean = false
    var reserved: Boolean = false
    var timeOfCheckIn: Timestamp? = null
    var timeOfReserve: Timestamp? = null
}