package com.example.turingparking.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turingparking.classes.ParkingList
import com.example.turingparking.databinding.ListItemParkingBinding
import com.example.turingparking.helpers.ParkingListClickListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ParkingListRecyclerViewAdapter(
    private val values: List<ParkingList>, private val parkingListClickListener: ParkingListClickListener)
 : RecyclerView.Adapter<ParkingListRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var storage: FirebaseStorage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        storage = Firebase.storage
        return ViewHolder(
            ListItemParkingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parking = values[position]

        holder.name.text = parking.name
        val imageRef = storage.getReferenceFromUrl(parking.imageUri)
        imageRef.downloadUrl.addOnSuccessListener {uri->
            val imageURL = uri.toString()
            Glide.with(context)
                .load(imageURL)
                .into(holder.image)
        }
        holder.address.text = parking.address
        val usedSpots = parking.usedSpots
        val spots = parking.spots
        val height = holder.redProgressBar.measuredHeight
        val greenBarHeight = ((spots - usedSpots)*height)/spots
        val width = 48
//        val bitmapRedBar = BitmapFactory.decodeResource(context.resources, R.drawable.round_background_red)
//        //val bitmapRedBarDraw = ResourcesCompat.getDrawable(context.resources, R.drawable.round_background_red, null) as GradientDrawable
//        //val bitmapRedBar = bitmapRedBarDraw.toBitmap()
//        val redBar = Bitmap.createScaledBitmap(bitmapRedBar, width, height, false)
//        holder.redProgressBar.setImageBitmap(redBar)
//        val bitmapGreenBar = BitmapFactory.decodeResource(context.resources, R.drawable.round_background_green)
//        //val bitmapGreenBarDraw = ResourcesCompat.getDrawable(context.resources, R.drawable.round_background_green, null) as GradientDrawable
//        //val bitmapGreenBar = bitmapGreenBarDraw.toBitmap()
//        val greenBar = Bitmap.createScaledBitmap(bitmapGreenBar, width, greenBarHeight, false)
//        holder.greenProgressBar.setImageBitmap(greenBar)


//        holder.spotProgressBar.layoutParams = ConstraintLayout.LayoutParams(width, height)
//        holder.spotProgressBar.layoutParams = ConstraintLayout.LayoutParams

//        holder.spotBackProgressBar.layoutParams = ConstraintLayout.LayoutParams(48, 60)
//        holder.spotBackProgressBar.requestLayout()
//        holder.spotProgressBar.layoutParams = ConstraintLayout.LayoutParams(48, barHeight)
//        holder.spotProgressBar.requestLayout()

        holder.item.setOnClickListener {
            parkingListClickListener.onParkingListItemClick(it, parking.id)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemParkingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image: ImageView = binding.parkingListImageView
        val name: TextView = binding.parkingListNameTextView
        val address: TextView = binding.parkingListAddressTextView
        val redProgressBar: ImageView = binding.spotsProgressBarRed
        val greenProgressBar: ImageView = binding.spotsProgressBarGreen
        val item: ConstraintLayout = binding.parkingListItem

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }

    companion object {
        private const val TAG = "ParkingListRecyclerView"
    }

}