package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R

/**
 * A simple [Fragment] subclass.
 * Use the [AddParkingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddParkingFragment : Fragment() {

    private val viewModel: AddViewModel by activityViewModels()
    private lateinit var nameEdit: EditText
    private lateinit var cnpjEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.intanceNewParking()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_add_parking, container, false)
        nameEdit = fragmentView.findViewById<View>(R.id.editName) as EditText
        cnpjEdit = fragmentView.findViewById<View>(R.id.editCnpj) as EditText


        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
            val name = nameEdit.editableText.toString()
            // TODO: check if cnpj is valid
            val cnpj = cnpjEdit.editableText.toString()
            if (name.isEmpty() || cnpj.isEmpty()){
                Toast.makeText(
                    this.requireContext(),
                    "Por Favor preencha todos os dados!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else{
                viewModel.addNameAndCnpj(name, cnpj)
                fragmentView.findNavController().navigate(R.id.nav_add_address)
            }
        }

        return fragmentView
    }

    companion object {
        private const val TAG = "AddFragment"
    }
}