package com.example.turingparking.classes

class Transaction (var type: Int, var name: String, var date: Long, var value: Double) {

    override fun toString(): String {
        return "Transaction(type=$type, name='$name', date=$date, value=$value)"
    }
}
