package com.example.turingparking.firebase_classes

class Wallet(var userId: String) {

    var id: String = ""
    var currentValue: Double = 0.0

    override fun toString(): String {
        return "Wallet(userId='$userId', id='$id', currentValue=$currentValue)"
    }
}