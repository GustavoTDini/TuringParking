package com.example.turingparking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turingparking.firebase_classes.Wallet
import com.example.turingparking.user_fragments.AddCarFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class InsertPromoActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth

        setContentView(R.layout.activity_insert_promo)

        val insertCodeEditView = findViewById<EditText>(R.id.insert_code_edit_text)

        val confirmButton = findViewById<Button>(R.id.insert_code_confirm_button)
        confirmButton.setOnClickListener {
            val insertedCode = insertCodeEditView.editableText.toString()
            val userEmail = auth.currentUser?.email
            db.collection("promoCodes").whereEqualTo("email", userEmail)
                .whereEqualTo("code", insertedCode)
                .get().addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "Código Inválido", Toast.LENGTH_SHORT).show()
                    } else {
                        val promoCode = documents.documents[0]
                        val userId = auth.currentUser?.uid.toString()
                        val promoCodeId = promoCode["id"] as String
                        db.collection("wallets").whereEqualTo("userId", userId).get()
                            .addOnSuccessListener { walletDocument ->
                                if (walletDocument.isEmpty) {
                                    val wallet = Wallet(userId)
                                    val id = UUID.randomUUID().toString()
                                    wallet.id = id
                                    wallet.currentValue = 0.0
                                    db.collection("wallets").document(id).set(wallet)
                                        .addOnSuccessListener {
                                            addPromoValueToWallet(
                                                wallet.id,
                                                promoCodeId
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                AddCarFragment.TAG,
                                                "Error writing document",
                                                e
                                            )
                                        }

                                } else {
                                    val walletId = walletDocument.documents[0]["id"].toString()
                                    addPromoValueToWallet(walletId, promoCodeId)
                                }
                            }
                    }
                }
        }


        val nextButton = findViewById<Button>(R.id.insert_code_next_button)
        nextButton.setOnClickListener {
            goToLogin()
        }

    }

    private fun addPromoValueToWallet(
        walletId: String,
        promoCodeId: String
    ) {
        val incrementValue = 20.00
        db.collection("wallets").document(walletId).update(
            "currentValue", FieldValue.increment(incrementValue)
        ).addOnSuccessListener {
            db.collection("promoCodes").document(promoCodeId)
                .update("active", false).addOnSuccessListener {
                    goToLogin()
                }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}