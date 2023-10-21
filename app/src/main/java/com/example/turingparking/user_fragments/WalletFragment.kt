package com.example.turingparking.user_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.TransactionListRecyclerViewAdapter
import com.example.turingparking.classes.Transaction
import com.example.turingparking.firebase_classes.Wallet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency
import java.util.UUID


class WalletFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var wallet: Wallet

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
        val fragmentView =  inflater.inflate(R.layout.fragment_wallet, container, false)

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")

        val valueTextView = fragmentView.findViewById<TextView>(R.id.wallet_value_text_view)

        val currentUser = auth.currentUser
        if(currentUser != null){
            wallet = Wallet(currentUser.uid)
            val userid = currentUser.uid
            db.collection("wallets")
                .whereEqualTo("userId", userid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty){
                        val id = UUID.randomUUID().toString()
                        wallet.id = id
                        wallet.currentValue = 0.0
                        db.collection("wallets").document(id).set(wallet)
                            .addOnSuccessListener {
                                Log.d(AddCarFragment.TAG, "DocumentSnapshot successfully written!")
                            }
                            .addOnFailureListener { e -> Log.w(AddCarFragment.TAG, "Error writing document", e) }
                    }
                    else{
                        val data = documents.documents[0].data
                        if (data != null) {
                            wallet.id = data["id"].toString()
                            wallet.currentValue = data["currentValue"] as Double
                            val value = format.format(wallet.currentValue)
                            valueTextView.text = value.toString()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
                }
        }

        val addPixButton = fragmentView.findViewById<ImageButton>(R.id.go_to_pix)

        addPixButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("walletId", wallet.id)
            fragmentView.findNavController().navigate(R.id.nav_pix, bundle)
        }

        val transactionList = fragmentView.findViewById<RecyclerView>(R.id.transaction_list_recycler_view)
        val emptyWallet = fragmentView.findViewById<ImageView>(R.id.empty_wallet_image)
        
        createTransactionList(transactionList, emptyWallet)

        return fragmentView
    }

    private fun createTransactionList(listView: RecyclerView, emptyView: ImageView){
        val list = ArrayList<Transaction>()
        val currentUser = auth.currentUser
        val userid = currentUser?.uid
        db.collection("pix").whereEqualTo("userId", userid)
            .get().addOnSuccessListener { pixes ->
                for (pix in pixes) {
                    val pixData = pix.data
                    val type = 0
                    val value = pixData["value"] as Double
                    val date = pixData["date"] as Long
                    val name = "Recarga PIX"
                    val pix = Transaction(type, name, date, value)
                    list.add(pix)
                }
                db.collection("stops").whereEqualTo("userId", userid).whereEqualTo("finalized", true)
                    .get().addOnSuccessListener { stops ->
                        for (stop in stops) {
                            val stopData = stop.data
                            val type = 1
                            val value = stopData["cost"] as Double
                            val date = stopData["timeOfCheckIn"] as Long
                            db.collection("parkings").document(stopData["parkingId"].toString())
                                .get().addOnSuccessListener { parkingDoc ->
                                    val parking = parkingDoc.data
                                    val parkingName = parking?.get("name") as String
                                    val name = "Gasto no estacionamento $parkingName"
                                    val cost = Transaction(type, name, date, -value)
                                    list.add(cost)
                                    
                                    val columnCount = list.size

                                    if (columnCount == 0) {
                                        emptyView.visibility = View.VISIBLE
                                        listView.visibility = View.GONE
                                    } else {
                                        emptyView.visibility = View.GONE
                                        listView.adapter = TransactionListRecyclerViewAdapter(list.sortedBy { transaction -> transaction.date})
                                        listView.visibility = View.VISIBLE
                                    }


                                }
                        }
                    }


            }
            .addOnFailureListener { exception ->
                Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
            }

    }

    companion object {
        val TAG = "WalletFragment"
    }
}
