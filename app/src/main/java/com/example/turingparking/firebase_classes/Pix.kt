package com.example.turingparking.firebase_classes

class Pix (var userId: String) {

    var id: String = ""
    var walletId = ""
    var value: Double = 0.0
    var date: Long = 0

    override fun toString(): String {
        return "Pix(walletId='$walletId', id='$id', value=$value, date=$date)"
    }
}