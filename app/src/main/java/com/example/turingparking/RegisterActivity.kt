package com.example.turingparking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.data.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginEditText = findViewById<EditText>(R.id.loginRegister)
        val passwordEditText = findViewById<EditText>(R.id.passwordRegister)
        val passwordConfirmEditText = findViewById<EditText>(R.id.passwordConfirmRegister)

        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener{
            if (loginEditText.text.isEmpty() ||
                passwordEditText.text.isEmpty()||
                passwordConfirmEditText.text.isEmpty() ){
                Toast.makeText(this@RegisterActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.editableText.toString() != passwordConfirmEditText.editableText.toString()){
                Toast.makeText(this@RegisterActivity, "As senhas não conferem", Toast.LENGTH_SHORT).show()
            }else{
                saveUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString(), this)

            }
        }
    }

    private fun saveUser(login: String, password: String, context: Context){
        val user = User(login,password)
        lifecycleScope.launch {
            MyApplication.database?.userDao()?.insert(user)
            val userTeste = MyApplication.database?.userDao()?.getAll()
            Log.d("SAVEUSER", userTeste.toString())
            Toast.makeText(this@RegisterActivity, "Salvo", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}