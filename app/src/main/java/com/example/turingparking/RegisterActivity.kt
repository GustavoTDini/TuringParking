package com.example.turingparking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.turingparking.firebase_classes.User
import com.example.turingparking.firebase_classes.Wallet
import com.example.turingparking.helpers.Helpers
import com.example.turingparking.helpers.Helpers.Companion.isPasswordValid
import com.example.turingparking.network.MailHelpers
import com.example.turingparking.user_fragments.AddCarFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {

    var code = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var createCodeTimeStamp by Delegates.notNull<Long>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        db = Firebase.firestore

        val loginEditText = findViewById<EditText>(R.id.loginRegister)
        val passwordEditText = findViewById<EditText>(R.id.passwordRegister)
        val passwordConfirmEditText = findViewById<EditText>(R.id.passwordConfirmRegister)
        val confirmCodeEditText = findViewById<EditText>(R.id.codeInput)
        val registerLayout = findViewById<LinearLayout>(R.id.registerLayout)
        val confirmLayout = findViewById<LinearLayout>(R.id.confirmLayout)
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        var exists = false

        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener {
            val login = loginEditText.editableText
            val password = passwordEditText.editableText
            val confirmPassword = passwordConfirmEditText.editableText
            db.collection("users").whereEqualTo("email", login.toString()).get()
                .addOnSuccessListener { documents ->
                    exists = !documents.isEmpty
                    Log.w(TAG, "Query Finished $exists")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

            if (login.isEmpty() || password.isEmpty()|| confirmPassword.isEmpty() ){
                Toast.makeText(this@RegisterActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (password.toString() != confirmPassword.toString()){
                Toast.makeText(this@RegisterActivity, "As senhas não conferem", Toast.LENGTH_SHORT).show()
            }else if (!login.matches(emailRegex.toRegex())) {
                Toast.makeText(this@RegisterActivity, "Por Favor coloque um e-mail válido", Toast.LENGTH_SHORT).show()
            } else if (exists) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Usuário com email já existente!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isPasswordValid(passwordEditText.editableText.toString(), this)) {
                code = Helpers.createCode(5)
                createCodeTimeStamp = System.currentTimeMillis()
                MailHelpers.sendMailUsingVolley(
                    loginEditText.editableText.toString(),
                    code,
                    MailHelpers.register,
                    this
                )
                confirmLayout.visibility = View.VISIBLE
                registerLayout.visibility = View.GONE
            }
        }


        val returnButton = findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener{
            confirmLayout.visibility = View.GONE
            registerLayout.visibility = View.VISIBLE
        }

        val checkButton = findViewById<Button>(R.id.checkCodeBtn)
        checkButton.setOnClickListener{
            if (confirmCodeEditText.editableText.toString() == code && System.currentTimeMillis() <= createCodeTimeStamp + fiveMin){
                saveUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString())
            } else{
                Toast.makeText(this@RegisterActivity, "Código invalido", Toast.LENGTH_SHORT).show()
            }
        }

        val resendButton = findViewById<Button>(R.id.resendBtn)
        resendButton.setOnClickListener{
            code = Helpers.createCode(5)
            createCodeTimeStamp = System.currentTimeMillis()
            MailHelpers.sendMailUsingVolley(
                loginEditText.editableText.toString(),
                code,
                MailHelpers.register,
                this
            )
        }
    }

    private fun saveUser(login: String, password: String){
        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val uId = user?.uid
                    if (uId != null) {
                        val dbUser = User(uId, login)
                        db.collection("users").document(uId)
                            .set(dbUser)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                val wallet = Wallet(uId)
                                val id = UUID.randomUUID().toString()
                                wallet.id = id
                                wallet.currentValue = 0.0
                                db.collection("wallets").document(id).set(wallet)
                                    .addOnSuccessListener {
                                        Log.d(
                                            AddCarFragment.TAG,
                                            "DocumentSnapshot successfully written!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            AddCarFragment.TAG,
                                            "Error writing document",
                                            e
                                        )
                                    }
                                db.collection("promoCodes").whereEqualTo("email", login).get()
                                    .addOnSuccessListener { document ->
                                        val promos = document.documents
                                        if (promos.isEmpty()) {
                                            openLoginIntent()
                                        } else {
                                            openPromoIntent()
                                        }
                                    }
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Falha na autenticação",
                        Toast.LENGTH_SHORT,
                    ).show()
                    reload()
                }
            }
    }


    private fun openPromoIntent() {
        Toast.makeText(this@RegisterActivity, "Salvo", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, InsertPromoActivity::class.java)
        startActivity(intent)
    }

    private fun openLoginIntent() {
        Toast.makeText(this@RegisterActivity, "Salvo", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun reload() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "RegisterActivity"
        private const val fiveMin = 300000
    }

}