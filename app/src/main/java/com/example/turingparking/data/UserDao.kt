package com.example.turingparking.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE login LIKE :login AND " +
            "password LIKE :password LIMIT 1")
    suspend fun checkIfExists(login: String, password: String): User

    @Insert
    suspend fun insert(user: User)
}