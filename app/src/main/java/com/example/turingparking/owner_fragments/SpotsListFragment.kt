package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.SpotsListRecyclerViewAdapter
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.user_fragments.CarListFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SpotsListFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_spots_list, container, false)
        val listView = fragmentView.findViewById<RecyclerView>(R.id.spots_recycler_view)
        val emptyView = fragmentView.findViewById<LinearLayout>(R.id.empty_state_spots)
        val list = ArrayList<Spots>()
        val parkingId = arguments?.getString("parkingId")

        if (parkingId != null){
            db.collection("spots").whereEqualTo("parkingId", parkingId)
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val spotsData = document.data
                        val spots = Spots(parkingId)
                        spots.id = spotsData["id"] as String
                        spots.preferential = spotsData["preferential"] as Boolean
                        spots.electric = spotsData["electric"] as Boolean
                        spots.carId = spotsData["carId"] as String
                        spots.occupied = spotsData["occupied"] as Boolean
                        spots.reserved = spotsData["reserved"] as Boolean
                        spots.timeOfCheckIn = spotsData["timeOfCheckIn"] as Long
                        spots.timeOfReserve = spotsData["timeOfReserve"] as Long
                        spots.timeOfLeave = spotsData["timeOfLeave"] as Long
                        list.add(spots)
                    }
                    val columnCount = list.size
                    Log.d(ParkingListFragment.TAG, "onCreateView: $columnCount")

                    if (columnCount == 0) {
                        emptyView.visibility = View.VISIBLE
                        listView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        listView.adapter = SpotsListRecyclerViewAdapter(list)
                        listView.visibility = View.VISIBLE
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
                }

        }


        return fragmentView
    }

    companion object
}