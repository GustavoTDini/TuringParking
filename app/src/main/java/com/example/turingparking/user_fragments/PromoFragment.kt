package com.example.turingparking.user_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.PromoCode
import com.example.turingparking.helpers.Helpers
import com.example.turingparking.network.MailHelpers
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 * Use the [PromoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PromoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var promoEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_promo, container, false)
        val registerButton = view.findViewById<Button>(R.id.promoBtn)
        promoEditText = view.findViewById(R.id.emailPromo)
        registerButton.setOnClickListener {
            createSendPromoCode(promoEditText.editableText.toString())
        }
        return view
    }

    fun createSendPromoCode(email: String) {
        val code = Helpers.createCode(6)
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { userDocuments ->
                if (userDocuments.isEmpty) {
                    db.collection("promoCodes").whereEqualTo("email", email).get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                val promoId = UUID.randomUUID().toString()
                                val promoCode = PromoCode(promoId, code, email, true)
                                db.collection("promoCodes").document(promoId).set(
                                    promoCode
                                ).addOnSuccessListener {
                                    MailHelpers.sendMailUsingVolley(
                                        email,
                                        code,
                                        MailHelpers.promo,
                                        this.requireContext()
                                    )
                                    Toast.makeText(
                                        requireContext(),
                                        "Código Enviado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    promoEditText.editableText.clear()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Este e-mail já recebeu um código",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Este e-mail já está cadastrado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

}