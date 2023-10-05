package com.example.turingparking.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.turingparking.R
import com.example.turingparking.StartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [ExitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExitFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_exit, container, false)
        val exitButton = fragmentView.findViewById<View>(R.id.exit_button) as Button

        exitButton.setOnClickListener(View.OnClickListener {
            Log.d("ExitFrag", "Teste_exit")
            auth.signOut()
            val intent = Intent(this.requireContext(), StartActivity::class.java)
            startActivity(intent)
        })
        return fragmentView
    }
}