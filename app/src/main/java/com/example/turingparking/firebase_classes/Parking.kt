package com.example.turingparking.firebase_classes


class Parking(var userId: String) {

    var id: String = ""
    var imageUri: String = ""
    var name: String = ""
    var cnpj: String = ""
    var cep: String = ""
    var addressStreet: String = ""
    var addressNumber: String = ""
    var addressComplement: String = ""
    var addressDistrict: String = ""
    var addressCity: String = ""
    var addressState: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var openHour: String = "0000"
    var closeHour: String = "0000"
    var twentyFour: Boolean = false
    var insured: Boolean = false
    var priceFor15: Double = 0.0
    var priceForHour: Double = 0.0
    var priceFor4Hour: Double = 0.0
    var priceFor24Hour: Double = 0.0
    var priceForNight: Double = 0.0
    var spots: Int = 0

    override fun toString(): String {
        return "Parking(id='$id', userId='$userId', imageUri='$imageUri', name='$name', cnpj='$cnpj', cep='$cep', addressStreet='$addressStreet', addressNumber='$addressNumber', addressComplement='$addressComplement', addressDistrict='$addressDistrict', addressCity='$addressCity', addressState='$addressState', latitude=$latitude, longitude=$longitude, openHour=$openHour, closeHour=$closeHour, twentyfour=$twentyFour, insured=$insured, priceFor15=$priceFor15, priceForHour=$priceForHour, priceFor4Hour=$priceFor4Hour, priceFor24Hour=$priceFor24Hour, priceForNight=$priceForNight,spots=$spots)"
    }

}