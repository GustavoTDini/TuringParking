package com.example.turingparking.user_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 * Use the [AddCarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCarFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var selectedType = 0
    private var selectedColor = 0
    private lateinit var typeSpinner: Spinner
    private lateinit var colorSpinner: Spinner
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var handicap = false
    private var electric = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView =  inflater.inflate(R.layout.fragment_add_car, container, false)
        val nickTextView = fragmentView.findViewById<TextView>(R.id.edit_nick)
        val plateTextView = fragmentView.findViewById<TextView>(R.id.edit_plate)

        typeSpinner = fragmentView.findViewById(R.id.spinner_type)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }
        typeSpinner.onItemSelectedListener = this
        colorSpinner = fragmentView.findViewById(R.id.spinner_color)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.colors_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            colorSpinner.adapter = adapter
        }
        colorSpinner.onItemSelectedListener = this

        val handicapSwitch = fragmentView.findViewById<View>(R.id.switch_handicap) as SwitchCompat
        handicapSwitch.isChecked = handicap
        handicapSwitch.setOnCheckedChangeListener { _, checked ->
            handicap = checked
        }

        val electricSwitch = fragmentView.findViewById<View>(R.id.switch_electric) as SwitchCompat
        electricSwitch.isChecked = electric
        electricSwitch.setOnCheckedChangeListener { _, checked ->
            electric = checked
        }

        val addButton = fragmentView.findViewById<View>(R.id.add_car_button) as Button
        addButton.setOnClickListener {
            val nick = nickTextView.editableText.toString()
            val plate = plateTextView.editableText.toString()
            saveCarToFirebase(nick, plate, fragmentView)
        }

        return fragmentView
    }

    private fun saveCarToFirebase(nick :String, plate: String, view: View) {
        val car = auth.currentUser?.uid?.let { it1 -> Car(it1) }
        if (car != null) {
            car.color = selectedColor
            car.type = selectedType
            car.nick = nick
            car.plate = plate
            car.electric = electric
            car.handicap = handicap
            val id = UUID.randomUUID().toString()
            car.id = id

            db.collection("cars").document(id)
                .set(car)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    view.findNavController().navigate(R.id.nav_cars_list)
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }
    companion object{
        val TAG = "AddCarFragment"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spinner_type -> {
                selectedType = position
            }
            R.id.spinner_color -> {
                selectedColor = position
            }
            else -> {
                Log.d(TAG, "Não há spinner com esse ID")
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d(TAG, "Nada Selecionado")
    }

}