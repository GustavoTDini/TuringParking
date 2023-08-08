package com.example.turingparking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.data.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    var code = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginEditText = findViewById<EditText>(R.id.loginRegister)
        val passwordEditText = findViewById<EditText>(R.id.passwordRegister)
        val passwordConfirmEditText = findViewById<EditText>(R.id.passwordConfirmRegister)
        val confirmCodeEditText = findViewById<EditText>(R.id.codeInput)
        val registerLayout = findViewById<LinearLayout>(R.id.registerLayout)
        val confirmLayout = findViewById<LinearLayout>(R.id.confirmLayout)

        val registerButton = findViewById<Button>(R.id.registerBtn)
        registerButton.setOnClickListener{
            if (loginEditText.text.isEmpty() ||
                passwordEditText.text.isEmpty()||
                passwordConfirmEditText.text.isEmpty() ){
                Toast.makeText(this@RegisterActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.editableText.toString() != passwordConfirmEditText.editableText.toString()){
                Toast.makeText(this@RegisterActivity, "As senhas não conferem", Toast.LENGTH_SHORT).show()
            }else{
                confirmLayout.visibility = View.VISIBLE
                registerLayout.visibility = View.GONE
                sendEmailCode(loginEditText.editableText.toString())

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
                saveUser(loginEditText.editableText.toString(), passwordEditText.editableText.toString(), this)
            } else{
                Toast.makeText(this@RegisterActivity, "Código invalido", Toast.LENGTH_SHORT).show()
            }
        }

        val resendButton = findViewById<Button>(R.id.resendBtn)
        resendButton.setOnClickListener{
            sendEmailCode(loginEditText.editableText.toString())
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

    private fun sendEmailCode(login: String) {
        createCode()
        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, login)
            putExtra(Intent.EXTRA_SUBJECT, "Seu código de validação do TuringParking")
            putExtra(Intent.EXTRA_TEXT,
                "Este é por enquanto um teste, no app final o email será enviado pelo backend, atraves de uma API, formatado em html \n " +
                    "O Código de validação é: \n" +
                        code)
        }
        Toast.makeText(this@RegisterActivity, code, Toast.LENGTH_SHORT).show()
        startActivity(mailIntent)
    }

    private fun createCode(){
        val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
        code = List(5) { charPool.random() }.joinToString("")
    }


}