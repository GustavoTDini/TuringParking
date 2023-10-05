package com.example.turingparking.owner_fragments

import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.turingparking.BuildConfig
import com.example.turingparking.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [AddAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAddressFragment : Fragment() {

    private val viewModel: AddViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var geocoder: Geocoder
    private lateinit var cepEdit: EditText
    private lateinit var streetEdit: EditText
    private lateinit var numberEdit: EditText
    private lateinit var complementEdit: EditText
    private lateinit var districtEdit: EditText
    private lateinit var cityEdit: EditText
    private lateinit var stateEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        geocoder = Geocoder(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_add_address, container, false)
        streetEdit = fragmentView.findViewById<View>(R.id.editStreet) as EditText
        numberEdit = fragmentView.findViewById<View>(R.id.editNumber) as EditText
        districtEdit = fragmentView.findViewById<View>(R.id.editDistrict) as EditText
        complementEdit = fragmentView.findViewById<View>(R.id.editComplement) as EditText
        cityEdit = fragmentView.findViewById<View>(R.id.editCity) as EditText
        stateEdit = fragmentView.findViewById<View>(R.id.editState) as EditText

        // Cep Select Functions
        cepEdit = fragmentView.findViewById<View>(R.id.editCep) as EditText
        cepEdit.setOnEditorActionListener { _, actionId, _ ->

            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    requestAddressFromCep(cepEdit.editableText.toString())
                    true
                }
                else -> false
            }
        }

        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
            val cep = cepEdit.editableText.toString()
            val street = streetEdit.editableText.toString()
            val number = numberEdit.editableText.toString()
            val district = districtEdit.editableText.toString()
            val complement = complementEdit.editableText.toString()
            val city = cityEdit.editableText.toString()
            val state = stateEdit.editableText.toString()

            viewModel.addAddress(cep, street,number, complement, district, state, city)
            requestLatLngFromAddress(fragmentView)
        }

        return fragmentView
    }

    fun requestAddressFromCep(cep: String) {
        val queue: RequestQueue = Volley.newRequestQueue(this.requireContext())
        val url = "https://brasilaberto.com/api/v1/zipcode/${cep}"
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                val result = response.getJSONObject("result")
                streetEdit.text =
                    Editable.Factory.getInstance().newEditable(result.getString("street"))
                districtEdit.text =
                    Editable.Factory.getInstance().newEditable(result.getString("district"))
                cityEdit.text = Editable.Factory.getInstance().newEditable(result.getString("city"))
                stateEdit.text =
                    Editable.Factory.getInstance().newEditable(result.getString("state"))

            } catch (e: Exception) {
                // on below line we are
                // handling our exception.
                e.printStackTrace()
            }

        }, { error ->
            // this method is called when we get
            // any error while fetching data from our API
            Log.e("TAG", "RESPONSE IS $error")
            // in this case we are simply displaying a toast message.
            Toast.makeText(context, "Falha na solicitação de HTTPS", Toast.LENGTH_SHORT)
                .show()
        })
        // at last we are adding
        // our request to our queue.
        queue.add(request)
    }

    fun requestLatLngFromAddress(fragmentView: View) {
        val apiKey = BuildConfig.MAPS_API_KEY
        val address = "${streetEdit.text}, numero ${numberEdit.text}, ${districtEdit.text}, ${cityEdit.text} - ${stateEdit.text}"
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=${address}&key=${apiKey}"
        // creating a new variable for our request queue
        val queue = Volley.newRequestQueue(context)
        // making a string request on below line.
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                val resultArray = response.getJSONArray("results")
                val result = resultArray.getJSONObject(0)
                val geometry = result.getJSONObject("geometry")
                val location = geometry.getJSONObject("location")
                val lat = location.getDouble("lat")
                val lng = location.getDouble("lng")
                viewModel.addCoordinates(lat, lng)
                fragmentView.findNavController().navigate(R.id.nav_add_map)
            } catch (e: Exception) {
                // on below line we are
                // handling our exception.
                e.printStackTrace()
            }
        }, { error ->
            // this method is called when we get
            // any error while fetching data from our API
            Log.e(TAG, "Coordinates error: $error")
            // in this case we are simply displaying a toast message.
            Toast.makeText(context, "Falha na solicitação de HTTPS", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(request)

    }

    companion object {
        private const val TAG = "AddAddressFragment"
    }
}