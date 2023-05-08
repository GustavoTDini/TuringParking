package com.example.turingparking.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Parking (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "parking_id")
    val parkingId: Int,

    @ColumnInfo(name = "nome_parking")
    val parkingName: String,

    @ColumnInfo(name = "image_url")
    val photoUrl: String,

    @ColumnInfo(name = "vagas")
    val vagas: Int,

    @ColumnInfo(name = "ocupadas")
    val ocupadas: Int,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "preco")
    val preco: Float,

    @ColumnInfo(name = "seguro")
    val seguro: Boolean,
    )
{
    constructor(nome: String,
                imageUrl:String,
                vagas: Int,
                ocupadas: Int,
                latitude: Double,
                longitude: Double,
                preco: Float,
                seguro: Boolean) : this(0,nome, imageUrl, vagas, ocupadas, latitude, longitude, preco, seguro)
}