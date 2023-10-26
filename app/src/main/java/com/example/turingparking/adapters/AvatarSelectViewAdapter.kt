package com.example.turingparking.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.databinding.ListItemAvatarBinding
import com.example.turingparking.helpers.AvatarClickListener
import com.example.turingparking.helpers.UIHelpers

class AvatarSelectViewAdapter(private val avatarClickListener: AvatarClickListener, private var currentAvatar: Int
) : RecyclerView.Adapter<AvatarSelectViewAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ListItemAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.avatarImageView.setImageResource(UIHelpers.avatarArray[position])
        holder.avatarFrame.setBackgroundColor(context.getColor(R.color.tertiary))
        if (position == currentAvatar){
            holder.avatarFrame.setBackgroundColor(context.getColor(R.color.secondary_container))
        }

        holder.avatarItem.setOnClickListener {
            currentAvatar = position
            avatarClickListener.onAvatarListItemClick(it, position)
        }
    }



    override fun getItemCount(): Int = UIHelpers.avatarArray.size

    inner class ViewHolder(binding: ListItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatarImageView: ImageView = binding.avatarImageView
        val avatarFrame: FrameLayout = binding.avatarListFrame
        val avatarItem: FrameLayout = binding.avatarListItem

        override fun toString(): String {
            return super.toString() + " '" + avatarImageView.toString() + "'"
        }

    }

    companion object {
        private const val TAG = "AvatarSelectViewAdapter"
    }


}
