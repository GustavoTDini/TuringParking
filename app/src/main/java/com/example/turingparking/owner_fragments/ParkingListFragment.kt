package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.ParkingListRecyclerViewAdapter
import com.example.turingparking.classes.ParkingList
import com.example.turingparking.firebase_classes.Parking
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.helpers.ParkingListClickInterface
import com.example.turingparking.user_fragments.CarListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class ParkingListFragment : Fragment(), ParkingListClickInterface {

    private var columnCount = 0
    private var parkingList = ArrayList<Parking>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_parking_list, container, false)
        val listView = fragmentView.findViewById<RecyclerView>(R.id.parking_recycler_view)
        val emptyView = fragmentView.findViewById<LinearLayout>(R.id.empty_state_parking)
        val list = ArrayList<ParkingList>()
        val currentUser = auth.currentUser
        val userid = currentUser?.uid
        db.collection("parkings").whereEqualTo("userId", userid)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val parkingData = document.data
                    val parking = ParkingList(userid.toString())
                    parking.name = parkingData["name"] as String
                    val addressStreet = parkingData["addressStreet"] as String
                    val addressNumber = parkingData["addressNumber"] as String
                    val addressComplement = parkingData["addressComplement"] as String
                    val addressDistrict = parkingData["addressDistrict"] as String
                    val addressCity = parkingData["addressCity"] as String
                    val addressState = parkingData["addressState"] as String

                    parking.address =
                        "$addressStreet, $addressNumber $addressComplement - $addressDistrict. $addressCity - $addressState"
                    parking.imageUri = parkingData["imageUri"] as String
                    parking.id = parkingData["id"] as String
                    val spots = parkingData["spots"] as Long
                    parking.spots = spots.toInt()
                    val electricSpotsLong = parkingData["spots"] as Long
                    val electricSpots = spots.toInt()
                    val handicapSpotsLong = parkingData["spots"] as Long
                    val handicapSpots = spots.toInt()
                    db.collection("spots").whereEqualTo("parkingId", parking.id)
                        .get().addOnSuccessListener { documents ->
                            if (documents.size() < parking.spots) {
                                Log.d(TAG, "onCreateView: ${parking.spots}")
                                Log.d(TAG, "onCreateView: ${documents.size()}")
                                val toAdd =
                                    parking.spots - documents.size() - handicapSpots - electricSpots
                                var handicap = handicapSpots
                                var electric = electricSpots
                                for (i in 1..handicap) {
                                    val spot = Spots(parking.id)
                                    val id = UUID.randomUUID().toString()
                                    spot.id = id
                                    spot.preferential = true
                                    db.collection("spots").document(id).set(spot)
                                }
                                for (i in 1..electricSpots) {
                                    val spot = Spots(parking.id)
                                    val id = UUID.randomUUID().toString()
                                    spot.id = id
                                    spot.electric = true
                                    db.collection("spots").document(id).set(spot)
                                }
                                for (i in 1..toAdd) {
                                    val spot = Spots(parking.id)
                                    val id = UUID.randomUUID().toString()
                                    spot.id = id
                                    db.collection("spots").document(id).set(spot)
                                }


                            }
                        }


                list.add(parking)
            }
        val columnCount = list.size
        Log.d(TAG, "onCreateView: $columnCount")

        if (columnCount == 0) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.adapter = ParkingListRecyclerViewAdapter(list, this)
            listView.visibility = View.VISIBLE
        }
            }.addOnFailureListener { exception ->
                Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
            }
        return fragmentView
    }

    companion object {
        val TAG = "ParkingListFragment"
    }

    override fun onParkingListItemClick(view: View, id: String) {
        val bundle = Bundle()
        bundle.putString("parkingId", id)
        fragmentView.findNavController().navigate(R.id.nav_spots, bundle)
    }
}