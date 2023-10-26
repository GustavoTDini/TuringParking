package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.TransactionListRecyclerViewAdapter
import com.example.turingparking.classes.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency


class FinanceReportFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val transactionList = ArrayList<Transaction>()
    private var totalValue:Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        val fragmentView =  inflater.inflate(R.layout.fragment_finance_report, container, false)
        val totalTextView = fragmentView.findViewById<TextView>(R.id.finance_report_value_text_view)
        val emptyView = fragmentView.findViewById<ImageView>(R.id.empty_finance_image)
        val financeList = fragmentView.findViewById<RecyclerView>(R.id.finance_report_list_recycler_view)

        val userId = auth.currentUser?.uid.toString()
        
        db.collection("parkings").whereEqualTo("userId", userId).get().addOnSuccessListener {documentsParkings ->
            for (parking in documentsParkings){
                db.collection("stops").whereEqualTo("parkingId", parking.id).whereEqualTo("finalized", true).get().addOnSuccessListener {stops->
                    for (stop in stops){
                        val stopData = stop.data
                        val type = 2
                        val value = stopData["cost"] as Double
                        totalValue += value
                        val date = stopData["timeOfCheckIn"] as Long
                        val carId = stopData["carId"] as String
                        db.collection("cars").document(carId).get().addOnSuccessListener {documentCar ->
                            val carData = documentCar.data
                            val carPlate = carData?.get("plate") as String
                            val name = "Estacionado o carro de placa $carPlate"
                            val transaction = Transaction(type,name, date, value)
                            transactionList.add(transaction)
                            if (transactionList.size == 0) {
                                emptyView.visibility = View.VISIBLE
                                financeList.visibility = View.GONE
                            } else {
                                emptyView.visibility = View.GONE
                                financeList.adapter = TransactionListRecyclerViewAdapter(transactionList.sortedBy { transaction -> transaction.date})
                                totalTextView.text = format.format(totalValue)
                                financeList.visibility = View.VISIBLE
                            }
                        }

                    }
                }
            }

        }
        
        return fragmentView
    }

    companion object {
        private const val TAG = "FinanceReportFragment"
    }
}