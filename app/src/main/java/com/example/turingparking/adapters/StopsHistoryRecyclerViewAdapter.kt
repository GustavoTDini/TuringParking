package com.example.turingparking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.data.StopParking
import com.example.turingparking.databinding.ListItemHistoryBinding
import java.text.NumberFormat
import java.util.Currency

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 */
class StopsHistoryRecyclerViewAdapter(
    private val values: List<StopParking>
) : RecyclerView.Adapter<StopsHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ListItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("BRL")
        holder.datetime.text = item.dateTime
        holder.nome.text = item.nome
        holder.preco.text = format.format(item.preco)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val datetime: TextView = binding.dateTime
        val nome: TextView = binding.parkingName
        val preco: TextView = binding.preco

        override fun toString(): String {
            return super.toString() + " '" + nome.text + "'"
        }
    }

}