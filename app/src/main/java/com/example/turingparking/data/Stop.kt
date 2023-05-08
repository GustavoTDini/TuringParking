package com.example.turingparking.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stop (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stop_id")
    val stopId: Int = 0,

    @ColumnInfo(name = "parking_id")
    val parkingId: Int,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: String,
)
{
    constructor(parkingId: Int,
                userId: Int,
                dateTime: String) : this(0,parkingId, userId, dateTime)
}