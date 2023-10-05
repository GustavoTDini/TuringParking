package com.example.turingparking.owner_fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass.
 * Use the [AddMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: AddViewModel by activityViewModels()
    private var mGoogleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_add_map, container, false)
        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
                fragmentView.findNavController().navigate(R.id.nav_add_photo)
            }
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.checkMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this )
    }

    companion object {
        private const val TAG = "AddMapFragment"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        setLocation()
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {

        val position  = viewModel.getCoordinates()
        mGoogleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                position, 15f
            )
        )
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title(viewModel.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_turing_parking_map))
                .draggable(false)
        )
    }
}