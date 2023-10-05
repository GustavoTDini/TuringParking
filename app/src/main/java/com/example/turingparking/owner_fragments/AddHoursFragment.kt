package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 * Use the [AddHoursFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddHoursFragment : Fragment() {

    private val viewModel: AddViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView =  inflater.inflate(R.layout.fragment_add_hours, container, false)

        var openHour = "0000"
        var closeHour = "0000"
        var twentyfour = false
        val f: NumberFormat = DecimalFormat("00")

        val openTimePicker = fragmentView.findViewById<View>(R.id.open_hour_time_picker) as TimePicker
        openTimePicker.setIs24HourView(true)
        openTimePicker.hour = 0
        openTimePicker.minute = 0
        openTimePicker.setOnTimeChangedListener { _, hour, min ->
            openHour = "${f.format(hour)}${f.format(min)}"
        }

        val closeTimePicker = fragmentView.findViewById<View>(R.id.close_hour_time_picker) as TimePicker
        closeTimePicker.setIs24HourView(true)
        closeTimePicker.hour = 0
        closeTimePicker.minute = 0
        closeTimePicker.setOnTimeChangedListener { _, hour, min ->
            closeHour = "${f.format(hour)}${f.format(min)}"
        }

        val twentyFourSwitch = fragmentView.findViewById<View>(R.id.twenty_four_switch) as SwitchCompat
        twentyFourSwitch.setOnCheckedChangeListener { _, checked ->
            twentyfour = checked
            if (checked){
                closeTimePicker.hour = 0
                closeTimePicker.minute = 0
                openTimePicker.hour = 0
                openTimePicker.minute = 0
                openHour = "0000"
                closeHour = "0000"
                closeTimePicker.isEnabled = false
                openTimePicker.isEnabled = false
            } else{
                closeTimePicker.isEnabled = true
                openTimePicker.isEnabled = true
            }
        }

        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {
            viewModel.addHours(openHour, closeHour, twentyfour)
            fragmentView.findNavController().navigate(R.id.nav_add_finance)
        }

        return fragmentView
    }

    companion object {
        private const val TAG = "AddHoursFragment"
    }
}