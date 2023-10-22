package com.example.turingparking.owner_fragments

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.SpotsListRecyclerViewAdapter
import com.example.turingparking.firebase_classes.Car
import com.example.turingparking.firebase_classes.Spots
import com.example.turingparking.helpers.FirebaseHelpers
import com.example.turingparking.helpers.Helpers.Companion.defineCost
import com.example.turingparking.helpers.Helpers.Companion.definePriority
import com.example.turingparking.helpers.SpotListClickListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SpotsListFragment : Fragment(), SpotListClickListener {

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
        val spotList = ArrayList<Spots>()
        val parkingId = arguments?.getString("parkingId")
        if (parkingId != null){
            getSpots(parkingId, spotList, emptyView, listView)
        }
        return fragmentView
    }

    private fun getSpots(
        parkingId: String,
        list: ArrayList<Spots>,
        emptyView: LinearLayout,
        listView: RecyclerView
    ) {
        db.collection("spots")
            .whereEqualTo("parkingId", parkingId)
            .orderBy("priority")
            .addSnapshotListener{documents, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (documents != null) {
                    list.clear()
                    for (document in documents) {
                        val spot = getSpot(document, parkingId)
                        list.add(spot)
                    }
                }
                val columnCount = list.size
                setVisibility(columnCount, emptyView, listView, list)
            }
    }


    private fun setVisibility(
        columnCount: Int,
        emptyView: LinearLayout,
        listView: RecyclerView,
        list: ArrayList<Spots>
    ) {
        if (columnCount == 0) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.adapter = SpotsListRecyclerViewAdapter(list, this)
            listView.visibility = View.VISIBLE
        }
    }

    private fun getSpot(
        document: QueryDocumentSnapshot,
        parkingId: String
    ): Spots {
        val spotsData = document.data
        val spot = Spots(parkingId)
        spot.id = spotsData["id"] as String
        spot.reserveId = spotsData["reserveId"] as String
        spot.preferential = spotsData["preferential"] as Boolean
        spot.electric = spotsData["electric"] as Boolean
        spot.carId = spotsData["carId"] as String
        spot.occupied = spotsData["occupied"] as Boolean
        spot.reserved = spotsData["reserved"] as Boolean
        spot.timeOfCheckIn = spotsData["timeOfCheckIn"] as Long
        spot.timeOfReserve = spotsData["timeOfReserve"] as Long
        spot.timeOfLeave = spotsData["timeOfLeave"] as Long
        spot.priority = definePriority(spot.electric, spot.preferential, spot.occupied, spot.reserved)
        return spot
    }

    override fun onSpotListItemClick(
        view: View,
        spot: Spots,
        car: Car?
    ) {
        val currentTime = System.currentTimeMillis()
        var usedSpot = "usedSpots"
        if (spot.electric){
            usedSpot = "usedElectricSpots"
        } else if (spot.preferential){
            usedSpot = "usedHandicapSpots"
        }
        if (car != null){
            if (spot.reserved){
                carArriveDialog(car, spot, currentTime)
            } else if (spot.occupied){
                carLeaveDialog(car, spot, currentTime, usedSpot)
            }
        } else{
            var status = "Vaga Livre - Nunca ocupada!"
            if (spot.timeOfLeave.toInt() > 0){
                val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale("pt-BR"))
                val timeFormat = SimpleDateFormat("hh:mm:ss", Locale("pt-BR"))
                status = "Vaga Livre - Ultimo carro saiu as  ${timeFormat.format(spot.timeOfLeave)} do dia ${dateFormat.format(spot.timeOfLeave)}}"
            }
            Toast.makeText(requireContext(), status, Toast.LENGTH_LONG).show()
        }
    }

    private fun carLeaveDialog(
        car: Car,
        spot: Spots,
        currentTime: Long,
        usedSpot: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        val carType = resources.getStringArray(R.array.types_array)[car.type]
        val carColor = resources.getStringArray(R.array.colors_array)[car.color]
        builder.setMessage("Confirma que o $carType $carColor de placa ${car.plate} está saindo?")
            .setCancelable(false)
            .setPositiveButton("Sim") { dialog, _ ->
                db.collection("parkings").document(spot.parkingId).get()
                    .addOnSuccessListener { document ->
                        val parking = document.data
                        if (parking != null) {
                            val priceFor15 = parking["priceFor15"] as Double
                            val priceForHour = parking["priceForHour"] as Double
                            val priceFor24Hours = parking["priceFor24Hour"] as Double
                            val priceForNight = parking["priceForNight"] as Double
                            val cost = defineCost(
                                spot.timeOfCheckIn,
                                spot.timeOfLeave,
                                priceFor15,
                                priceForHour,
                                priceFor24Hours,
                                priceForNight
                            )
                            val priority = definePriority(spot.electric, spot.preferential, false, false)
                            FirebaseHelpers.spendWallet(car.userId, cost)
                            FirebaseHelpers.updateLeaveSpot(spot.id, currentTime, priority)
                            FirebaseHelpers.updateLeaveStop(spot.reserveId, currentTime, cost)
                            FirebaseHelpers.decrementUsedParkingSpot(spot.parkingId, usedSpot)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Deu algo errado na requisição, tente novamente!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.d(TAG, "onSpotListItemClick: Occupied")
                        dialog.dismiss()
                    }
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun carArriveDialog(
        car: Car,
        spot: Spots,
        currentTime: Long,
    ) {
        val builder = AlertDialog.Builder(requireContext())
        val carType = resources.getStringArray(R.array.types_array)[car.type]
        val carColor = resources.getStringArray(R.array.colors_array)[car.color]
        builder.setMessage("Confirma que o $carType $carColor de placa ${car.plate} chegou?")
            .setCancelable(false)
            .setPositiveButton("Sim") { dialog, _ ->
                val priority = definePriority(spot.electric, spot.preferential, true, false)
                FirebaseHelpers.updateCheckInSpot(spot.id, currentTime, priority)
                FirebaseHelpers.updateCheckInStop(spot.id, currentTime)
                Log.d(TAG, "onSpotListItemClick: Reserved")
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    companion object{
        private const val TAG = "SpotsListFragment"
    }

}