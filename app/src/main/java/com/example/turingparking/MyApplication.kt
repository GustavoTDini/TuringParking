package com.example.turingparking

import android.app.Application
import com.example.turingparking.data.TuringDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        var database: TuringDB? = null
    }

    override fun onCreate() {
        super.onCreate()
        //Room
        database = TuringDB.getDatabase(this, applicationScope)
    }
}