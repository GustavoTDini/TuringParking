package com.example.turingparking

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.turingparking.owner.MainOwnerActivity
import com.example.turingparking.user.MainUserActivity

class SelectUserTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)

        // calling the action bar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userButton = findViewById<ImageButton>(R.id.user_button)
        userButton.setOnClickListener {
            val intent = Intent(this, MainUserActivity::class.java)
            startActivity(intent)
        }

        val ownerButton = findViewById<ImageButton>(R.id.owner_button)
        ownerButton.setOnClickListener {
            val intent = Intent(this, MainOwnerActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}