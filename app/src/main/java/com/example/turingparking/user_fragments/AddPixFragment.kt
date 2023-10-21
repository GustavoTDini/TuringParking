package com.example.turingparking.user_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Pix
import com.example.turingparking.helpers.FirebaseHelpers
import com.example.turingparking.network.HttpRequestHelpers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AddPixFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_add_pix, container, false)

        var value = 20.00

        val button20Pix = fragmentView.findViewById<Button>(R.id.pix_20_button)
        val button50Pix = fragmentView.findViewById<Button>(R.id.pix_50_button)
        val button100Pix = fragmentView.findViewById<Button>(R.id.pix_100_button)
        val button200Pix = fragmentView.findViewById<Button>(R.id.pix_200_button)

        button20Pix.setOnClickListener {
            value = 20.00
            button20Pix.setBackgroundColor(resources.getColor(R.color.primary))
            button50Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button100Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button200Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
        }

        button50Pix.setOnClickListener {
            value = 50.00
            button20Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button50Pix.setBackgroundColor(resources.getColor(R.color.primary))
            button100Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button200Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
        }

        button100Pix.setOnClickListener {
            value = 100.00
            button20Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button50Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button100Pix.setBackgroundColor(resources.getColor(R.color.primary))
            button200Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
        }

        button200Pix.setOnClickListener {
            value = 200.00
            button20Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button50Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button100Pix.setBackgroundColor(resources.getColor(R.color.tertiary))
            button200Pix.setBackgroundColor(resources.getColor(R.color.primary))
        }

        val qRCodeImageView = fragmentView.findViewById<ImageView>(R.id.qr_code_image_view)

        val qRCodeView = fragmentView.findViewById<LinearLayout>(R.id.qr_code_view)

        val buttonGenerateQRCode = fragmentView.findViewById<Button>(R.id.generate_qrcode_buttom)
        buttonGenerateQRCode.setOnClickListener {
            HttpRequestHelpers.getQRCodeUsingVolley(value, qRCodeImageView, requireContext())
            qRCodeView.visibility = View.VISIBLE
        }

        val payPixButtom = fragmentView.findViewById<Button>(R.id.pay_pix_buttom)
        payPixButtom.setOnClickListener {
            val currentUser = auth.currentUser
            if(currentUser != null) {
                val pix = Pix(currentUser.uid)
                val id = UUID.randomUUID().toString()
                pix.id = id
                pix.value = value
                pix.date = System.currentTimeMillis()
                val walletId = arguments?.getString("walletId")
                if (walletId != null) {
                    pix.walletId = walletId
                    FirebaseHelpers.addPIx(pix, fragmentView, requireContext())
                }
            }
        }
        return fragmentView
    }



    companion object {
        val TAG = "AddPixFragment"
    }
}