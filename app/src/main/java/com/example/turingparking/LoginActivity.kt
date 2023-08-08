package com.example.turingparking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.data.User
import kotlinx.coroutines.launch
import kotlin.random.Random


class LoginActivity : AppCompatActivity() {

    var recaptchaCode = ""
    var loginLayout: LinearLayout? = null
    var recaptchaLayout: LinearLayout? = null
    var recaptchaImage: ImageView? = null
    var recapctchaEditText: EditText? = null
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginEditText = findViewById<EditText>(R.id.login)
        val passwordEditText = findViewById<EditText>(R.id.password)
        loginLayout = findViewById(R.id.loginLayout)
        recaptchaLayout = findViewById(R.id.recaptchaLayout)
        recaptchaImage = findViewById(R.id.recapcthaImage)
        recapctchaEditText = findViewById(R.id.recapcthaInput)

        val loginButton = findViewById<Button>(R.id.loginBtn)
        loginButton.setOnClickListener{
            if (loginEditText.text.isEmpty() ||
                passwordEditText.text.isEmpty() ){
                Toast.makeText(this@LoginActivity, "Por Favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                testUser(
                    loginEditText.editableText.toString(),
                    passwordEditText.editableText.toString(),
                    this@LoginActivity
                )
            }
        }

        val confirmRecaptchaButton = findViewById<Button>(R.id.confirmRecaptchaBtn)
        confirmRecaptchaButton.setOnClickListener{
            user?.let { it1 -> checkRecaptcha(it1, this) }
        }

        val reloadRecaptchaButton = findViewById<ImageButton>(R.id.reloadRecaptchaBtn)
        reloadRecaptchaButton.setOnClickListener{
            generateRecaptcha()
        }
    }

    private fun testUser(
        login: String,
        password: String,
        context: Context
    ) {
        lifecycleScope.launch {
            val loggedUser = MyApplication.database?.userDao()?.checkIfExists(login,password)
            if (loggedUser!=null) {
                loginLayout?.visibility  = View.GONE
                recaptchaLayout?.visibility = View.VISIBLE
                user = loggedUser
                generateRecaptcha()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Login ou senha estão incorretos",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

    private fun loginUser(user: User, context: Context) {
        val preferences = this@LoginActivity.getSharedPreferences("user_preferences", MODE_PRIVATE)
        with(preferences.edit()) {
            putInt("UserId", user.userId)
            apply()
        }
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("DiscouragedApi")
    private fun generateRecaptcha() {
        val randomCode = Random.nextInt(0, 15)
        val imageUri = "@drawable/image_" + randomCode
        val imageResource = resources.getIdentifier(imageUri, null, packageName)
        recaptchaImage?.setImageResource(imageResource)
        val codeUri = "@string/recaptcha" + randomCode
        val stringResource = resources.getIdentifier(codeUri, null, packageName)
        recaptchaCode = resources.getString(stringResource)
    }

    private fun checkRecaptcha(user: User, context: Context) {
        if (recapctchaEditText?.editableText.toString() == recaptchaCode){
            loginUser(user, context)
        }else {
            Toast.makeText(context, "Código invalido", Toast.LENGTH_SHORT).show()
        }
    }
}