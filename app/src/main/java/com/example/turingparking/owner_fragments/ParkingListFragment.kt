package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.ParkingListRecyclerViewAdapter
import com.example.turingparking.classes.ParkingList
import com.example.turingparking.helpers.ParkingListClickListener
import com.example.turingparking.user_fragments.CarListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ParkingListFragment : Fragment(), ParkingListClickListener {

    private var columnCount = 0
    private var parkingList = ArrayList<ParkingList>()
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
        val waitView = fragmentView.findViewById<ProgressBar>(R.id.wait_state_parking)
        val currentUser = auth.currentUser
        val userid = currentUser?.uid
        if (userid !== null){
            getParkingList(userid, waitView, emptyView, listView)
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

    private fun getParkingList(
        userid: String?,
        waitView: ProgressBar,
        emptyView: LinearLayout,
        listView: RecyclerView
    ) {
        db.collection("parkings").whereEqualTo("userId", userid)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val parking = getParking(document, userid)
                    parkingList.add(parking)
                }
                columnCount = parkingList.size
                waitView.visibility = View.GONE
                setVisibility(emptyView, listView)
            }.addOnFailureListener { exception ->
                Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
            }
    }

    private fun getParking(
        document: QueryDocumentSnapshot,
        userid: String?
    ): ParkingList {
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
        val usedSpots = parkingData["usedSpots"] as Long
        val usedElectricSpots = parkingData["usedElectricSpots"] as Long
        val usedHandicapSpots = parkingData["usedHandicapSpots"] as Long
        parking.usedSpots = usedSpots.toInt() + usedElectricSpots.toInt() + usedHandicapSpots.toInt()

//        val electricSpotsLong = parkingData["electricSpots"] as Long
//        val electricSpots = electricSpotsLong.toInt()
//        val handicapSpotsLong = parkingData["handicapSpots"] as Long
//        val handicapSpots = handicapSpotsLong.toInt()
//        FirebaseHelpers.createParkingSpots(parking, handicapSpots, electricSpots)
        return parking
    }

    private fun setVisibility(
        emptyView: LinearLayout,
        listView: RecyclerView
    ) {
        if (columnCount == 0) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.adapter = ParkingListRecyclerViewAdapter(parkingList, this)
            listView.visibility = View.VISIBLE
        }
    }

}