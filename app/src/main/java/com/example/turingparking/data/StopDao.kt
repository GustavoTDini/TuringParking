package com.example.turingparking.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StopDao {
    @Query("SELECT * FROM stop")
    suspend fun getAll(): List<Stop>

    @Query("SELECT * FROM stop WHERE user_id LIKE :id")
    suspend fun getStopsFromUser(id: Int): List<Stop>

    @Insert
    suspend fun insert(stop: Stop)
}