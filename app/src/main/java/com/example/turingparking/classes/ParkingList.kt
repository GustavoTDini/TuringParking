package com.example.turingparking.classes

class ParkingList(var userId: String) {

    var id: String = ""
    var imageUri: String = ""
    var name: String = ""
    var address: String = ""
    var spots: Int = 0
    var usedSpots: Int = 0

    override fun toString(): String {
        return "ParkingList(userId='$userId', id='$id', imageUri='$imageUri', name='$name', address='$address', spots=$spots, usedSpots=$usedSpots)"
    }


}