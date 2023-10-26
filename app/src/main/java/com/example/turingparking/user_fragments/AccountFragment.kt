package com.example.turingparking.user_fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.helpers.UIHelpers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentEmail: String
    private var selectedLanguage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_account, container, false)
        val selectAvatarButton = fragmentView.findViewById<Button>(R.id.account_avatar_change_button)
        val selectAddCarButton = fragmentView.findViewById<Button>(R.id.account_add_change_car_button)
        val avatarImageView = fragmentView.findViewById<ImageView>(R.id.account_avatar_image_view)
        val carImageView = fragmentView.findViewById<ImageView>(R.id.account_car_image_view)
        val electricCarImageView = fragmentView.findViewById<ImageView>(R.id.account_electric_icon_image_view)
        val handicapCarImageView = fragmentView.findViewById<ImageView>(R.id.account_handicap_icon_image_view)
        val nameEditText = fragmentView.findViewById<EditText>(R.id.account_name_edit_text)
        val cpfEditText = fragmentView.findViewById<EditText>(R.id.account_cpf_edit_text)
        val emailEditText = fragmentView.findViewById<EditText>(R.id.account_email_edit_text)
        val emailUpdateButton = fragmentView.findViewById<Button>(R.id.account_update_email_button)
        val codeEditText = fragmentView.findViewById<EditText>(R.id.account_code_edit_text)
        val languageSpinner = fragmentView.findViewById<Spinner>(R.id.account_language_spinner)
        val favoritesRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.account_favorites_recycler)
        val updateButton = fragmentView.findViewById<Button>(R.id.account_update_button)

        val userId = auth.currentUser?.uid

        if (userId != null){
            db.collection("users").document(userId).get().addOnSuccessListener {document->
                val user = document.data
                if (user != null){
                    val userName = user["nome"] as String
                    val userAvatar = user["avatar"] as Long
                    val userCarId = user["currentCar"] as String
                    val userLanguage = user["language"] as Long
                    selectedLanguage = userLanguage.toInt()
                    val userEmail = user["email"] as String
                    currentEmail = userEmail
                    val userCpf = user["cpf"] as String
                    val userFavorites = user["favorites"]
                    Log.d(TAG, "onCreateView: $userFavorites")
                    nameEditText.text = Editable.Factory.getInstance().newEditable(userName)
                    cpfEditText.text = Editable.Factory.getInstance().newEditable(userCpf)
                    emailEditText.text = Editable.Factory.getInstance().newEditable(userEmail)
                    if (emailEditText.editableText.toString() != currentEmail){
                        emailUpdateButton.visibility = View.VISIBLE
                    } else{
                        emailUpdateButton.visibility = View.GONE
                    }
                    emailEditText.setOnEditorActionListener { it, i, keyEvent ->
                        if(it.editableText.toString() != currentEmail){
                            emailUpdateButton.visibility = View.VISIBLE
                        } else{
                            emailUpdateButton.visibility = View.GONE
                        }
                        return@setOnEditorActionListener true
                    }
                    ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.languages_array,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        languageSpinner.adapter = adapter
                        languageSpinner.onItemSelectedListener = this
                    }
                    languageSpinner.setSelection(selectedLanguage)
                    emailUpdateButton.setOnClickListener{
                        it.visibility = View.GONE

                    }
                    avatarImageView.setImageResource(UIHelpers.avatarArray[userAvatar.toInt()])
                    db.collection("cars").document(userCarId).get().addOnSuccessListener { document->
                        val car = document.data
                        if (car != null){
                            val type = (car["type"] as Long).toInt()
                            val color = (car["color"] as Long).toInt()
                            val electric = car["electric"] as Boolean
                            val handicap = car["handicap"] as Boolean
                            carImageView.setImageResource(UIHelpers.getCarIcon(type, color))
                            if (electric){
                                electricCarImageView.visibility = View.VISIBLE
                            } else{
                                electricCarImageView.visibility = View.GONE
                            }
                            if (handicap){
                                handicapCarImageView.visibility = View.VISIBLE
                            } else{
                                handicapCarImageView.visibility = View.GONE
                            }
                            selectAddCarButton.text = "Trocar Carro"
                        } else{
                            selectAddCarButton.text = "Adicionar Carro"
                            carImageView.setImageResource(R.drawable.turing_car)
                            handicapCarImageView.visibility = View.GONE
                            electricCarImageView.visibility = View.GONE
                        }
                    }


                }

            }

        }

        selectAddCarButton.setOnClickListener{
            fragmentView.findNavController().navigate(R.id.nav_cars_list)
        }

        selectAvatarButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putInt("currentAvatar", 0)
            fragmentView.findNavController().navigate(R.id.nav_avatar, bundle)
        }

        updateButton.setOnClickListener{
            if (userId != null) {
                db.collection("users").document(userId)
                    .update("nome", nameEditText.editableText.toString(), "cpf", cpfEditText.editableText.toString(), "language", selectedLanguage)
                    .addOnSuccessListener {
                        fragmentView.findNavController().navigate(R.id.nav_map)
                    }
            }

        }



        return fragmentView
    }

    companion object {
        private const val TAG = "AccountFragment"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedLanguage = position
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d(TAG, "Nada Selecionado")
    }
}