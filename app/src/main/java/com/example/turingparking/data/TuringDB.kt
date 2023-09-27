package com.example.turingparking.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.turingparking.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Database(entities = [User::class, Stop::class, Parking::class], version = 3)
abstract class TuringDB: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun parkingDao(): ParkingDao
    abstract fun stopDao(): StopDao

    companion object {
        @Volatile
        private var INSTANCE: TuringDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TuringDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            Log.d("GetDatabase", "start ")
            if (INSTANCE == null) {
                Log.d("GetDatabase", "instance null ")
                synchronized(this) {
                    Log.d("GetDatabase", "synchronized")
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context, scope)
                }
            }
            Log.d("GetDatabase", "finish")
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context, scope: CoroutineScope): TuringDB {
            Log.d("buildDatabase", "start")
            return Room.databaseBuilder(
                context,
                TuringDB::class.java,
                "turing_database")
                .addCallback(TuringDatabaseCallback(scope))
                .build()
        }

    }

    private class TuringDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    Log.d("PARKINGS test", "Scope")
                    populateDatabase()
                }
            }
        }

        suspend fun populateDatabase(){
            val parkings = MyApplication.database?.parkingDao()?.getAll()
            if (parkings != null) {
                if (parkings.isEmpty()){
                    for (i in 1..200){
                        val nome = "Parking$i"
                        val url = "res/drawable/estacionamento.jpg"
                        val vagas = Random.nextInt(5,10)
                        val ocupadas = Random.nextInt(0, 4)
                        val lat = Random.nextDouble(-28.86863067551838,-22.25000039229986)
                        val long = Random.nextDouble(-51.52208877302552,-44.6384480697326)
                        val price = Random.nextDouble(0.0,10.0)
                        val seguro = Random.nextBoolean()
                        val newParking = Parking(nome, url, vagas, ocupadas, lat, long, price.toFloat(), seguro)
                        MyApplication.database?.parkingDao()?.insert(newParking)
                    }
                }
            }
        }
    }
}