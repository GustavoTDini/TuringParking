package com.example.turingparking.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "login")
    val userLogin: String,

    @ColumnInfo(name = "password")
    val userPassword: String
)
{
    constructor(login: String, password:String) : this(0,login, password)
}
