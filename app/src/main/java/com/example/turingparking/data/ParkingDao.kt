package com.example.turingparking.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ParkingDao {
    @Query("SELECT * FROM parking")
    suspend fun getAll(): List<Parking>

    @Insert
    suspend fun insert(parking: Parking)

    @Query("SELECT * FROM parking WHERE parking_id LIKE :id  LIMIT 1")
    suspend fun getParkingFromId(id: Int): Parking
}