package com.example.turingparking.network

import android.content.Context
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley


class HttpRequestHelpers{

    companion object {
        private val TAG = "HttpRequestHelpers"

        fun getQRCodeUsingVolley(value: Double, iv: ImageView, context: Context) {
            val baseUrl = "https://api.qrserver.com/v1/create-qr-code/"
            val sizeParam = "size=500x500"
            val titleParam = "title=PIX"
            val dataParam = "data=Pix de $value reais"
            val url = "$baseUrl?$sizeParam&$titleParam&$dataParam"
            // creating a new variable for our request queue
            val queue = Volley.newRequestQueue(context)
            // making a string request on below line.
            val request = ImageRequest(url,
                { response -> // callback
                    iv.setImageBitmap(response)
                }, 500, 500, null, null
            )
            queue.add(request)
        }



    }
}
