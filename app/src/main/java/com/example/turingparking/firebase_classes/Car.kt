package com.example.turingparking.firebase_classes

class Car(var userId: String) {

    var id: String = ""
    var nick: String = ""
    var plate: String = ""
    var electric: Boolean = false
    var handicap: Boolean = false
    var type: Int = 0
    var color: Int = 0

    override fun toString(): String {
        return "Car(userId='$userId', id='$id', nick='$nick', plate='$plate', electric=$electric, handicap=$handicap, type=$type, color=$color)"
    }

}