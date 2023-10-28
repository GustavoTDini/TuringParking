package com.example.turingparking.firebase_classes

class User(var userId: String, var email: String) {

    var avatar: Int = 0
    var cpf: String = ""
    var currentCar: String = ""
    var favorites: ArrayList<String> = ArrayList()
    var language: Int = 0
    var nome: String = ""

    override fun toString(): String {
        return "User(userId='$userId', email='$email', avatar=$avatar, cpf='$cpf', currentCar='$currentCar', favorites=$favorites, language=$language, nome='$nome')"
    }
}