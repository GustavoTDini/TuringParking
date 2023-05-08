package com.example.turingparking.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.MyApplication
import com.example.turingparking.R
import com.example.turingparking.StartActivity
import com.example.turingparking.adapters.HistoryRecyclerViewAdapter
import com.example.turingparking.data.Stop
import com.example.turingparking.data.StopParking
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HistoryFragment : Fragment() {

    private var columnCount = 0
    private var userId:Int = 0
    private lateinit var stopList: List<Stop>
    private var showList: ArrayList<StopParking> = ArrayList()
    private lateinit var list: RecyclerView
    private lateinit var empty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        empty = view.findViewById(R.id.empty)
        list = view.findViewById(R.id.list)
        return view
    }

    private fun getList() {
        val preferences = requireActivity().getSharedPreferences(
            "user_preferences",
            AppCompatActivity.MODE_PRIVATE
        )
        userId = preferences.getInt("UserId", -1)
        if (userId == -1) {
            Toast.makeText(requireActivity(), "Usuario não Logado", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), StartActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                stopList = MyApplication.database?.stopDao()?.getStopsFromUser(userId)!!
                for (stop in stopList) {
                    val parking =
                        MyApplication.database?.parkingDao()?.getParkingFromId(stop.parkingId)!!
                    val stopParking = StopParking(stop.dateTime, parking.parkingName, parking.preco)
                    showList.add(stopParking)
                    columnCount = showList.size
                }
            }
        }
    }

    private fun setViews(){
        if (columnCount == 0){
            list.visibility = View.INVISIBLE
            empty.visibility = View.VISIBLE
        } else{
            list.visibility = View.VISIBLE
            empty.visibility = View.INVISIBLE
            if (list is RecyclerView) {
                with(list) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = HistoryRecyclerViewAdapter(showList.toList())
                }
            }
        }
    }
}