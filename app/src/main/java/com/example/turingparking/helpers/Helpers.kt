package com.example.turingparking.helpers

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.turingparking.firebase_classes.Car
import java.io.ByteArrayOutputStream

class Helpers {
    companion object{

        private const val mili15min = 900000
        private const val miliHour = 3600000
        private const val mili24Hour = 86400000

        fun createImageFile(bitmapImage: Bitmap): ByteArray{
            val bos = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.PNG,0, bos)
            return bos.toByteArray()
        }

        fun defineCost(timeOfCheckIn: Long, timeOfLeave: Long, priceFor15: Double, priceForHour: Double, priceFor24Hours: Double, priceForNight: Double): Double{
            val totalTime = timeOfLeave - timeOfCheckIn
            if (priceFor15 > 0 && totalTime < mili15min){
                return priceFor15
            }
            val totalHours = (totalTime/ miliHour).toInt()
            if (totalHours > 24){
                val totalDays: Int = (totalHours/24)
                var remainHours: Int = (totalHours%24)
                if (priceFor24Hours > 0) {
                    return totalDays * priceFor24Hours + remainHours * priceForHour
                } else if (priceForNight > 0) {
                    remainHours += totalDays * 12
                    return totalDays * priceForNight + remainHours * priceForHour
                }
            }
            return totalHours * priceForHour
        }

        fun createCode(size: Int): String {
            val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
            return List(size) { charPool.random() }.joinToString("")
        }

        fun isPasswordValid(password: String, context: Context): Boolean {
            if (password.length < 8) {
                Toast.makeText(context, "A Senha deve ter no minimo 8 digitos", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            if (password.filter { it.isDigit() }.firstOrNull() == null) {
                Toast.makeText(context, "A Senha deve ter no minimo 1 numero", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            if (password.filter { it.isLetter() }.filter { it.isUpperCase() }
                    .firstOrNull() == null) {
                Toast.makeText(
                    context,
                    "A Senha deve ter no minimo 1 caractere maiusculo",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (password.filter { it.isLetter() }.filter { it.isLowerCase() }
                    .firstOrNull() == null) {
                Toast.makeText(
                    context,
                    "A Senha deve ter no minimo 1 caractere minusculo",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) {
                Toast.makeText(
                    context,
                    "A Senha deve ter no minimo 1 caractere especial",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            return true
        }

        fun definePriority(
            electric: Boolean,
            handicap: Boolean,
            occupied: Boolean,
            reserved: Boolean
        ): Int {
            return if (occupied) {
                if (electric) 1
                else if (handicap) 2
                else 3
            } else if (reserved) {
                if (electric) 4
                else if (handicap) 5
                else 6
            } else {
                if (electric) 7
                else if (handicap) 8
                else 9
            }
        }

        fun createCar(
            carData: Map<String, Any>,
            userid: String
        ): Car {
            val car = Car(userid)
            car.id = carData["id"] as String
            car.handicap = carData["handicap"] as Boolean
            car.electric = carData["electric"] as Boolean
            car.plate = carData["plate"] as String
            car.nick = carData["nick"] as String
            val type = carData["type"] as Long
            car.type = type.toInt()
            val color = carData["color"] as Long
            car.color = color.toInt()
            return car
        }

    }


}