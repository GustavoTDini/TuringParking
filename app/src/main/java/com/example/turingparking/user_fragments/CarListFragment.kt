package com.example.turingparking.user_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.CarListRecyclerViewAdapter
import com.example.turingparking.firebase_classes.Car
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * A fragment representing a list of Items.
 */
class CarListFragment : Fragment() {

    private var columnCount = 0
    private var carList = ArrayList<Car>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_list, container, false)
        val listView = view.findViewById<RecyclerView>(R.id.car_recycler_view)
        val emptyView = view.findViewById<LinearLayout>(R.id.empty_car_list)
        val currentUser = auth.currentUser
        carList.clear()
        if(currentUser != null){
            val userid = currentUser.uid
            db.collection("cars")
                .whereEqualTo("userId", userid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val carData = document.data
                        val car = Car(userid)
                        car.handicap = carData["handicap"] as Boolean
                        car.electric = carData["electric"] as Boolean
                        car.plate = carData["plate"] as String
                        car.nick = carData["nick"] as String
                        val type = carData["type"] as Long
                        car.type = type.toInt()
                        val color = carData["color"] as Long
                        car.color = color.toInt()
                        carList.add(car)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                    columnCount = carList.size
                    if (columnCount == 0){
                        emptyView.visibility = View.VISIBLE
                        listView.visibility = View.GONE
                    } else{
                        emptyView.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                    }
                    // Set the adapter
                    if (listView is RecyclerView) {
                        with(listView) {
                            LinearLayoutManager(context)
                            adapter = CarListRecyclerViewAdapter(carList)
                            addItemDecoration(DividerItemDecoration(context, horizontalFadingEdgeLength))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }

        val addCarButton = view.findViewById<FloatingActionButton>(R.id.add_car_fab)

        addCarButton.setOnClickListener {
            view.findNavController().navigate(R.id.nav_add_cars)
        }

        return view
    }

    companion object {
        val TAG = "CarListFragment"
    }
}