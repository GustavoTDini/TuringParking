package com.example.turingparking.user_fragments

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
import com.example.turingparking.adapters.StopsHistoryRecyclerViewAdapter
import com.example.turingparking.firebase_classes.Stops
import com.example.turingparking.helpers.HistoryListClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * A fragment representing a list of Items.
 */
class StopsHistoryFragment : Fragment(), HistoryListClickListener {

    private var columnCount = 0
    private var stopList: ArrayList<Stops> = ArrayList()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var fragmentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_history_list, container, false)
        val listView = fragmentView.findViewById<RecyclerView>(R.id.history_list)
        val emptyView = fragmentView.findViewById<LinearLayout>(R.id.empty_history)
        val waitView = fragmentView.findViewById<ProgressBar>(R.id.wait_state_history)
        val currentUser = auth.currentUser
        val userid = currentUser?.uid
        stopList.clear()
        if (userid !== null){
            getList(userid, waitView, emptyView, listView)
        }
        return fragmentView
    }

    private fun getList(userId: String, waitView: ProgressBar, emptyView: LinearLayout, listView: RecyclerView) {
        db.collection("stops").whereEqualTo("userId", userId).whereEqualTo("finalized", true)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val stop = getStops(document, userId)
                    stopList.add(stop)
                }
                columnCount = stopList.size
                Log.d(TAG, "getList: $columnCount")
                for(stop in stopList){
                    Log.d(TAG, "getList: $stop")
                }
                waitView.visibility = View.GONE
                setStopsListVisibility(emptyView, listView)
            }.addOnFailureListener { exception ->
                Log.w(CarListFragment.TAG, "Error getting documents: ", exception)
            }
    }

    private fun getStops(
        document: QueryDocumentSnapshot,
        userid: String?
    ): Stops {
        val stopData = document.data
        val stop = Stops(userid.toString())
        stop.id = stopData["id"] as String
        stop.parkingId = stopData["parkingId"] as String
        stop.timeOfCheckIn = stopData["timeOfCheckIn"] as Long
        stop.timeOfLeave = stopData["timeOfLeave"] as Long
        stop.cost = stopData["cost"] as Double
        val rating = stopData["rating"] as Number
        stop.rating = rating.toDouble()
        return stop
    }

    private fun setStopsListVisibility(
        emptyView: LinearLayout,
        listView: RecyclerView
    ) {
        if (columnCount == 0) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.adapter = StopsHistoryRecyclerViewAdapter(stopList, this)
            listView.visibility = View.VISIBLE
        }
    }
    
    companion object{
        private const val TAG = "StopsHistoryFragment"
    }

    override fun onHistoryListItemClick(view: View, id: String) {
        val bundle = Bundle()
        bundle.putString("stopId", id)
        fragmentView.findNavController().navigate(R.id.nav_spots_details, bundle)
    }
}