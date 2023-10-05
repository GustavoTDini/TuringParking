package com.example.turingparking.user_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.turingparking.R
import com.example.turingparking.network.MailHelpers

/**
 * A simple [Fragment] subclass.
 * Use the [PromoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PromoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_promo, container, false)
        val registerButton = view.findViewById<Button>(R.id.promoBtn)
        val promoEditText = view.findViewById<EditText>(R.id.emailPromo)
        registerButton.setOnClickListener{
            MailHelpers.postMailUsingVolley(promoEditText.editableText.toString(), "promo", this.requireContext())
            Toast.makeText(this.requireContext(), "Promoção entrará em breve", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}