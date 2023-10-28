package com.example.turingparking.adapters

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.turingparking.R
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
        Log.d(TAG, "onBindViewHolder used: $usedSpots")
        val spots = parking.spots
        Log.d(TAG, "onBindViewHolder spots: $spots")
        //val height = holder.redProgressBar.measuredHeight
        val height = 72
        Log.d(TAG, "onBindViewHolder height: $height")
        //Log.d(TAG, "onBindViewHolder: $height")
        val greenBarHeight = ((spots - usedSpots) * height) / spots
        //val greenBarHeight = height/2
        Log.d(TAG, "onBindViewHolder green: $greenBarHeight")
        val width = 36

//        val params = holder.greenProgressBar.layoutParams as ViewGroup.MarginLayoutParams
//        params.setMargins(0,greenBarHeight,16,8)
//        //apply params
//        holder.greenProgressBar.layoutParams = params
        //holder.greenProgressBar.requestLayout()

//        val newLayoutParams = holder.greenProgressBar.updateLayoutParams {
//            this.height = he
//        }
//        newLayoutParams.topMargin = 0
//        newLayoutParams.leftMargin = 0
//        newLayoutParams.rightMargin = 0
//        toolbar.setLayoutParams(newLayoutParams)

//        val  lp = ConstraintLayout.LayoutParams(
//            ConstraintLayout.LayoutParams.WRAP_CONTENT,
//            ConstraintLayout.LayoutParams.WRAP_CONTENT).setMargins( 0, height, 16,8)
//        holder.greenProgressBar.setMargins
//        holder.greenProgressBar.requestLayout()


//        fun View.setMargins(
//            left: Int = this.marginLeft,
//            top: Int = this.marginTop,
//            right: Int = this.marginRight,
//            bottom: Int = this.marginBottom,
//        ) {
//            layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
//                setMargins(left, top, right, bottom)
//            }
//        }

        //val bitmapRedBar = BitmapFactory.decodeResource(context.resources, R.drawable.round_background_red)
//        val bitmapRedBarDraw = ResourcesCompat.getDrawable(context.resources, R.drawable.red_bar, null) as Drawable
//        val bitmapRedBar = bitmapRedBarDraw?.toBitmap()
//        val redBar = bitmapRedBar?.let { Bitmap.createScaledBitmap(it, width, height, false) }
//        holder.redProgressBar.setImageDrawable(ScaleDrawable(bitmapRedBarDraw, Gravity.BOTTOM,
//            width.toFloat(), height.toFloat()
//        ))

        val bitmapRedBarDraw =
            ResourcesCompat.getDrawable(context.resources, R.drawable.red_bar, null)
        val bitmapRedBar = bitmapRedBarDraw?.toBitmap()
        val redBar = bitmapRedBar?.let { Bitmap.createScaledBitmap(it, width, height, false) }
        holder.redProgressBar.setImageBitmap(redBar)

        val bitmapGreenBarDraw =
            ResourcesCompat.getDrawable(context.resources, R.drawable.green_bar, null)
        val bitmapGreenBar = bitmapGreenBarDraw?.toBitmap()
        val greenBar =
            bitmapGreenBar?.let { Bitmap.createScaledBitmap(it, width, greenBarHeight, false) }
        holder.greenProgressBar.setImageBitmap(greenBar)
//        holder.greenProgressBar.layoutParams = (LayoutParams(32,50))
//        holder.greenProgressBar.requestLayout()


//            ScaleDrawable(bitmapGreenBarDraw, Gravity.BOTTOM,
//            width.toFloat(), greenBarHeight.toFloat()
//        )
//        )


//        //val bitmapGreenBar = BitmapFactory.decodeResource(context.resources, R.drawable.round_background_green)
//        val bitmapGreenBar = ResourcesCompat.getDrawable(context.resources, R.drawable.green_bar, null)
//        //val bitmapGreenBar = bitmapGreenBarDraw.toBitmap()
//        val greenBar = Bitmap.createScaledBitmap(bitmapGreenBar, width, greenBarHeight, false)
//        holder.greenProgressBar.setImageBitmap(greenBar)
//        bitmapGreenBarDraw.bounds = Rect(0,0,height, width)
//        bitmapGreenBarDraw.bounds = Rect(0,0,greenBarHeight, width)
//        holder.redProgressBar.setImageDrawable(bitmapRedBarDraw)
//        holder.greenProgressBar.setImageDrawable(bitmapGreenBarDraw)
        //
        //holder.greenProgressBar.setImageBitmap(ScaleDrawable(bitmapGreenBarDraw,Gravity.BOTTOM, 0.5f, 1f))
        //

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