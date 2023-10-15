package com.example.turingparking.owner_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.turingparking.R
import com.example.turingparking.firebase_classes.Parking
import kotlin.math.floor
import kotlin.math.round

/**
 * A simple [Fragment] subclass.
 * Use the [AddFinanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFinanceFragment : Fragment() {

    private val viewModel: AddViewModel by activityViewModels()
    private var price15minDouble: Double = 0.0
    private var price1HourDouble: Double = 0.0
    private var price4HoursDouble: Double = 0.0
    private var price24HoursDouble: Double = 0.0
    private var priceNightDouble: Double = 0.0
    private var spots = 1
    private var electricSpots = 0
    private var handicapSpots = 0
    private var insured = false
    private lateinit var parking: Parking
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parking = viewModel.getParking()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentView: View = inflater.inflate(R.layout.fragment_add_finance, container, false)

        // Control Variables

        price15minDouble = parking.priceFor15
        var price15min: Int = price15minDouble.toInt()
        var price15minCents: Double = price15minDouble - floor(price15minDouble)
        price1HourDouble = parking.priceForHour
        var price1Hour: Int = price1HourDouble.toInt()
        var price1HourCents: Double = price1HourDouble - floor(price1HourDouble)
        price4HoursDouble = parking.priceFor4Hour
        var price4Hours: Int = price4HoursDouble.toInt()
        var price4HoursCents: Double = price4HoursDouble - floor(price4HoursDouble)
        price24HoursDouble = parking.priceFor24Hour
        var price24Hours: Int = price24HoursDouble.toInt()
        var price24HoursCents: Double = price24HoursDouble - floor(price24HoursDouble)
        priceNightDouble = parking.priceForNight
        var priceNight: Int = priceNightDouble.toInt()
        var priceNightCents: Double = priceNightDouble - floor(priceNightDouble)


        // Control for 15min prices
        val price15minPicker = fragmentView.findViewById<View>(R.id.picker_15_min) as NumberPicker
        price15minPicker.minValue = 0
        price15minPicker.maxValue = 999
        price15minPicker.value = price15min
        price15minPicker.wrapSelectorWheel = true
        price15minPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price15min = newVal
            price15minDouble = (price15min + price15minCents)
        }

        val price15minCentsPicker = fragmentView.findViewById<View>(R.id.picker_15_min_cents) as NumberPicker
        price15minCentsPicker.setFormatter { i -> String.format("%02d", i) }
        price15minCentsPicker.minValue = 0
        price15minCentsPicker.maxValue = 99
        price15minCentsPicker.value = (price15minCents*100).toInt()
        price15minCentsPicker.wrapSelectorWheel = true
        price15minCentsPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price15minCents = (newVal.toDouble()/100)
            price15minDouble = (price15min + price15minCents)
        }

        val price15minSwitch = fragmentView.findViewById<View>(R.id.switch_15_min) as SwitchCompat
        price15minSwitch.isChecked = price15minDouble >= 0
        price15minSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                price15minPicker.isEnabled = true
                price15minCentsPicker.isEnabled = true
                price15minDouble = -0.0
            } else{
                price15minPicker.isEnabled = false
                price15minCentsPicker.isEnabled = false
                price15minDouble = -1.0
            }
            price15minPicker.value = 0
            price15minCentsPicker.value = 0
            price15min = 0
            price15minCents = 0.0
        }

        // Control for 1 Hour prices
        val price1HourPicker = fragmentView.findViewById<View>(R.id.picker_1_hour) as NumberPicker
        price1HourPicker.minValue = 0
        price1HourPicker.maxValue = 999
        price1HourPicker.value = price1Hour
        price1HourPicker.wrapSelectorWheel = true
        price1HourPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price1Hour = newVal
            price1HourDouble = (price1Hour + price1HourCents)
        }

        val price1HourCentsPicker = fragmentView.findViewById<View>(R.id.picker_1_hour_cents) as NumberPicker
        price1HourCentsPicker.setFormatter { i -> String.format("%02d", i) }
        price1HourCentsPicker.minValue = 0
        price1HourCentsPicker.maxValue = 99
        price1HourCentsPicker.value = (price15minCents*100).toInt()
        price1HourCentsPicker.wrapSelectorWheel = true
        price1HourCentsPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price1HourCents = (newVal.toDouble()/100)
            price1HourDouble = (price1Hour + price1HourCents)
        }

        val price1HourSwitch = fragmentView.findViewById<View>(R.id.switch_1_hour) as SwitchCompat
        price1HourSwitch.isChecked = price1HourDouble >= 0
        price1HourSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                price1HourPicker.isEnabled = true
                price1HourCentsPicker.isEnabled = true
                price1HourDouble = -0.0
            } else{
                price1HourPicker.isEnabled = false
                price1HourCentsPicker.isEnabled = false
                price1HourDouble = -1.0
            }
            price1HourPicker.value = 0
            price1HourCentsPicker.value = 0
            price1Hour = 0
            price1HourCents = 0.0
        }

        // Control for 4 Hours prices
        val price4HoursPicker = fragmentView.findViewById<View>(R.id.picker_4_hours) as NumberPicker
        price4HoursPicker.minValue = 0
        price4HoursPicker.maxValue = 999
        price4HoursPicker.value = price4Hours
        price4HoursPicker.wrapSelectorWheel = true
        price4HoursPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price4Hours = newVal
            price4HoursDouble = (price4Hours + price4HoursCents)
        }

        val price4HoursCentsPicker = fragmentView.findViewById<View>(R.id.picker_4_hours_cents) as NumberPicker
        price4HoursCentsPicker.setFormatter { i -> String.format("%02d", i) }
        price4HoursCentsPicker.minValue = 0
        price4HoursCentsPicker.maxValue = 99
        price4HoursCentsPicker.value = (price4HoursCents*100).toInt()
        price4HoursCentsPicker.wrapSelectorWheel = true
        price4HoursCentsPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price4HoursCents = (newVal.toDouble()/100)
            price4HoursDouble = (price15min + price15minCents)
        }

        val price4HoursSwitch = fragmentView.findViewById<View>(R.id.switch_4_hours) as SwitchCompat
        price4HoursSwitch.isChecked = price4HoursDouble >= 0
        price4HoursSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                price4HoursPicker.isEnabled = true
                price4HoursCentsPicker.isEnabled = true
                price4HoursDouble = -0.0
            } else{
                price4HoursPicker.isEnabled = false
                price4HoursCentsPicker.isEnabled = false
                price4HoursDouble = -1.0
            }
            price4HoursPicker.value = 0
            price4HoursCentsPicker.value = 0
            price4Hours = 0
            price4HoursCents = 0.0
        }

        // Control for 24 Hours prices
        val price24HoursPicker = fragmentView.findViewById<View>(R.id.picker_24_hours) as NumberPicker
        price24HoursPicker.minValue = 0
        price24HoursPicker.maxValue = 999
        price24HoursPicker.value = price24Hours
        price24HoursPicker.wrapSelectorWheel = true
        price24HoursPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price24Hours = newVal
            price24HoursDouble = (price24Hours + price24HoursCents)
        }

        val price24HoursCentsPicker = fragmentView.findViewById<View>(R.id.picker_24_hours_cents) as NumberPicker
        price24HoursCentsPicker.setFormatter { i -> String.format("%02d", i) }
        price24HoursCentsPicker.minValue = 0
        price24HoursCentsPicker.maxValue = 99
        price24HoursCentsPicker.value = (price24HoursCents*100).toInt()
        price24HoursCentsPicker.wrapSelectorWheel = true
        price24HoursCentsPicker.setOnValueChangedListener { _, _, newVal: Int ->
            price24HoursCents = (newVal.toDouble()/100)
            price24HoursDouble = (price4Hours + price4HoursCents)
        }

        val price24HoursView = fragmentView.findViewById<View>(R.id.view_24_hours) as LinearLayout
        val price24HoursSwitch = fragmentView.findViewById<View>(R.id.switch_24_hours) as SwitchCompat
        price24HoursSwitch.isChecked = price24HoursDouble >= 0
        price24HoursSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked){
                price24HoursPicker.isEnabled = true
                price24HoursCentsPicker.isEnabled = true
                price24HoursDouble = -0.0
            } else{
                price24HoursPicker.isEnabled = false
                price24HoursCentsPicker.isEnabled = false
                price24HoursDouble = -1.0
                price24Hours = 0
                price24HoursCents = 0.0
            }
            price24HoursPicker.value = 1
            price24HoursCentsPicker.value = 1
            price24Hours = 0
            price24HoursCents = 0.0
        }

        // Control for night prices
        val priceNightPicker = fragmentView.findViewById<View>(R.id.picker_night) as NumberPicker
        priceNightPicker.minValue = 0
        priceNightPicker.maxValue = 999
        priceNightPicker.value = priceNight
        priceNightPicker.wrapSelectorWheel = true
        priceNightPicker.setOnValueChangedListener { _, _, newVal: Int ->
            priceNight = newVal
            priceNightDouble = (priceNight + priceNightCents)
        }

        val priceNightCentsPicker = fragmentView.findViewById<View>(R.id.picker_night_cents) as NumberPicker
        priceNightCentsPicker.setFormatter { i -> String.format("%02d", i) }
        priceNightCentsPicker.minValue = 0
        priceNightCentsPicker.maxValue = 99
        priceNightCentsPicker.value = (priceNightCents*100).toInt()
        priceNightCentsPicker.wrapSelectorWheel = true
        priceNightCentsPicker.setOnValueChangedListener { _, _, newVal: Int ->
            priceNightCents = (newVal.toDouble()/100)
            priceNightDouble = (price4Hours + price4HoursCents)
        }

        val priceNightView = fragmentView.findViewById<View>(R.id.view_night) as LinearLayout
        val priceNightSwitch = fragmentView.findViewById<View>(R.id.switch_night) as SwitchCompat
        priceNightSwitch.isChecked = priceNightDouble >= 0
        priceNightSwitch.setOnCheckedChangeListener { _, checked ->
            priceNightPicker.value = 0
            priceNightCentsPicker.value = 0
            if (checked){
                priceNightPicker.isEnabled = true
                priceNightCentsPicker.isEnabled = true
                priceNightDouble = -0.0
            } else{
                priceNightPicker.isEnabled = false
                priceNightCentsPicker.isEnabled = false
                priceNightDouble = -1.0
            }

            priceNight = 0
            priceNightCents = 0.0
        }

        if (viewModel.is24hours()){
            priceNightView.visibility = View.GONE
            priceNightSwitch.visibility = View.GONE
            price24HoursView.visibility = View.VISIBLE
            price24HoursSwitch.visibility = View.VISIBLE
            priceNightDouble = -1.0
        } else{
            priceNightView.visibility = View.VISIBLE
            priceNightSwitch.visibility = View.VISIBLE
            price24HoursView.visibility = View.GONE
            price24HoursSwitch.visibility = View.GONE
            price24HoursDouble = -1.0
        }

        val insuranceSwitch = fragmentView.findViewById<View>(R.id.insurance_switch) as SwitchCompat
        insuranceSwitch.isChecked = insured
        setInsuredSwitchText(insured, insuranceSwitch)
        insuranceSwitch.setOnCheckedChangeListener { _, checked ->
            insured = checked
            setInsuredSwitchText(checked, insuranceSwitch)
        }

        // Spot Picker Functions
        val electricSpotPicker = fragmentView.findViewById<View>(R.id.electric_spots_picker) as NumberPicker
        electricSpotPicker.minValue = 0
        electricSpotPicker.maxValue = 999
        electricSpotPicker.value = electricSpots
        electricSpotPicker.wrapSelectorWheel = true
        electricSpotPicker.setOnValueChangedListener { _, _, newVal: Int ->
            electricSpots = newVal
        }

        // Spot Picker Functions
        val handicapSpotPicker = fragmentView.findViewById<View>(R.id.handicap_spots_picker) as NumberPicker
        handicapSpotPicker.minValue = spots/20
        handicapSpotPicker.maxValue = 999
        handicapSpotPicker.value = handicapSpots
        handicapSpotPicker.wrapSelectorWheel = true
        handicapSpotPicker.setOnValueChangedListener { _, _, newVal: Int ->
            handicapSpots = newVal
        }

        // Spot Picker Functions
        val spotPicker = fragmentView.findViewById<View>(R.id.spots_picker) as NumberPicker
        spotPicker.minValue = 1
        spotPicker.maxValue = 999
        spotPicker.value = spots
        spotPicker.wrapSelectorWheel = true
        spotPicker.setOnValueChangedListener { _, _, newVal: Int ->
            spots = newVal
            val minHandicapSpots = round((spots/20).toDouble()).toInt()
            if (handicapSpots < minHandicapSpots){
                handicapSpots = minHandicapSpots
                handicapSpotPicker.value = handicapSpots
            }
            handicapSpotPicker.minValue = minHandicapSpots
        }

        val nextButton = fragmentView.findViewById<View>(R.id.next_button) as Button
        nextButton.setOnClickListener {


            viewModel.addFinance(price15minDouble, price1HourDouble, price4HoursDouble, price24HoursDouble, priceNightDouble, insured, spots, electricSpots, handicapSpots)
            fragmentView.findNavController().navigate(R.id.nav_add_finish)
        }
        return fragmentView
    }

    private fun setInsuredSwitchText(
        checked: Boolean,
        insuranceSwitch: SwitchCompat
    ) {
        if (checked) {
            insuranceSwitch.text = "Com seguro"
        } else {
            insuranceSwitch.text = "Sem seguro"
        }
    }

    companion object {
        private const val TAG = "AddFinanceFragment"
    }
}