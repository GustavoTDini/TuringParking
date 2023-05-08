package com.example.turingparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Funcionalidade para o botão de Login
        val login_btn = findViewById<Button>(R.id.loginButton)
        login_btn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Funcionalidade para o botão do Facebook - ainda a ser implementada
        val facebook_btn = findViewById<Button>(R.id.facebookButton)
        facebook_btn.setOnClickListener{
            Toast.makeText(this@StartActivity, getString(R.string.facebook_toast), Toast.LENGTH_SHORT).show()
        }

        // Funcionalidade para o botão de register
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}