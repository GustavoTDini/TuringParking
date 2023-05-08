package com.example.turingparking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginEditText = findViewById<EditText>(R.id.login)
        val passwordEditText = findViewById<EditText>(R.id.password)

        val loginButton = findViewById<Button>(R.id.loginBtn)
        loginButton.setOnClickListener{
            if (loginEditText.text.isEmpty() ||
                passwordEditText.text.isEmpty() ){
                Toast.makeText(this@LoginActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                testUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString(), this)
            }
        }

    }

    private fun testUser(
        login : String,
        password: String,
        context: Context
    ) {
        lifecycleScope.launch {
            val user = MyApplication.database?.userDao()?.checkIfExists(login,password)
            if (user!=null) {
                val preferences = this@LoginActivity.getSharedPreferences("user_preferences", MODE_PRIVATE)
                with (preferences.edit()) {
                    putInt("UserId", user.userId)
                    apply()
                }
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Login ou senha estão incorretos",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }
}