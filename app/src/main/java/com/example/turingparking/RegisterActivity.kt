package com.example.turingparking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    var code = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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

        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener{
            if (loginEditText.text.isEmpty() ||
                passwordEditText.text.isEmpty()||
                passwordConfirmEditText.text.isEmpty() ){
                Toast.makeText(this@RegisterActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.editableText.toString() != passwordConfirmEditText.editableText.toString()){
                Toast.makeText(this@RegisterActivity, "As senhas não conferem", Toast.LENGTH_SHORT).show()
            }else if (!loginEditText.editableText.matches(emailRegex.toRegex())) {
                Toast.makeText(this@RegisterActivity, "Por Favor coloque um e-mail válido", Toast.LENGTH_SHORT).show()
            } else if (isPasswordValid(passwordEditText.editableText.toString())) {
                saveUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString())
            }
        }

        val returnButton = findViewById<Button>(R.id.returnBtn)
        returnButton.setOnClickListener{
            confirmLayout.visibility = View.GONE
            registerLayout.visibility = View.VISIBLE
        }

        val checkButton = findViewById<Button>(R.id.checkCodeBtn)
        checkButton.setOnClickListener{
            if (confirmCodeEditText.editableText.toString() == code){
                saveUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString())
            } else{
                Toast.makeText(this@RegisterActivity, "Código invalido", Toast.LENGTH_SHORT).show()
            }
        }

        val resendButton = findViewById<Button>(R.id.resendBtn)
        resendButton.setOnClickListener{
            //sendEmailCode(loginEditText.editableText.toString())
        }
    }

    private fun saveUser(login: String, password: String){
        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    openLoginIntent()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    reload()
                }
            }
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) {
            Toast.makeText(this@RegisterActivity, "A Senha deve ter no minimo 8 digitos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.filter { it.isDigit() }.firstOrNull() == null){
            Toast.makeText(this@RegisterActivity, "A Senha deve ter no minimo 1 numero", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null){
            Toast.makeText(this@RegisterActivity, "A Senha deve ter no minimo 1 caractere maiusculo", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null){
            Toast.makeText(this@RegisterActivity, "A Senha deve ter no minimo 1 caractere minusculo", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) {
            Toast.makeText(this@RegisterActivity, "A Senha deve ter no minimo 1 caractere especial", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createCode(){
        val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
        code = List(5) { charPool.random() }.joinToString("")
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
        private const val TAG = "EmailPassword"
    }


}