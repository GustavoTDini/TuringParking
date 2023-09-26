package com.example.turingparking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.turingparking.data.User
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    var recaptchaCode = ""


    var loginLayout: LinearLayout? = null
    var recaptchaLayout: LinearLayout? = null
    var recaptchaImage: ImageView? = null
    var recapctchaEditText: EditText? = null
    var user: User? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var recaptchaClient: RecaptchaClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        initializeRecaptchaClient()

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
                )
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToMain()
        }
    }

    private fun initializeRecaptchaClient() {
        val apiKey = {BuildConfig.RECAPTCHA_KEY}
        lifecycleScope.launch {
            Recaptcha.getClient(application, apiKey.toString())
                .onSuccess { client ->
                    Log.d("reCAPTCHA", "reCAPTCHA initialized")
                    recaptchaClient = client
                }
                .onFailure { exception ->
                    Log.d("reCAPTCHA", "Failed to initialize reCAPTCHA")
                    //returnToStart()
                }
        }
    }

    private fun testUser(
        login: String,
        password: String,
    ) {
        auth.signInWithEmailAndPassword(login, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    executeRecatchaTest()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    returnToStart()
                }
            }
    }

    private fun executeRecatchaTest() {
        lifecycleScope.launch {
            recaptchaClient
                .execute(RecaptchaAction.LOGIN)
                .onSuccess { token->
                    Toast.makeText(baseContext, "Usuário verificado pelo reCAPTCHA", Toast.LENGTH_SHORT).show()
                    Log.d("reCAPTCHA", token)
                    goToMain()
                }
                .onFailure { e ->
                    Log.d("reCAPTCHA", "Failed to initialize reCAPTCHA $e")
                    returnToStart()
                }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun returnToStart() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}