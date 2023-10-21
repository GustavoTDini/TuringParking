package com.example.turingparking.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.classes.Transaction
import com.example.turingparking.databinding.ListItemTransactionBinding
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Date

class TransactionListRecyclerViewAdapter(
    private val values: List<Transaction>
) : RecyclerView.Adapter<TransactionListRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        Log.d(TAG, "onCreateViewHolder: $parent")
        return ViewHolder(
            ListItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = values[position]

        Log.d(TAG, "onBindViewHolder: holder")

        val credit = 0
        val debit = 1

        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance()
        formatCurrency.maximumFractionDigits = 2
        formatCurrency.currency = Currency.getInstance("BRL")

        val formatDate: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        holder.name.text = transaction.name
        val date = Date(transaction.date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val dateText = "Dia: ${formatDate?.format(date)}"
        holder.date.text = dateText

        if (transaction.type == credit ){
            holder.type.setImageResource(R.drawable.pix_icon)
            holder.details.text = "Pix Realizado"
            val value = "+ ${formatCurrency.format(transaction.value)}"
            holder.value.text = value
            holder.value.setTextColor(context.resources.getColor((R.color.primary)))
        } else if (transaction.type == debit){
            holder.type.setImageResource(R.drawable.parking_icon)
            holder.details.text = "Estacionado"
            val value = formatCurrency.format(transaction.value)
            holder.value.text = value
            holder.value.setTextColor(context.resources.getColor((R.color.error)))
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val type: ImageView = binding.transactionImageTypeIcon
        val name: TextView = binding.transactionNameTextView
        val details: TextView = binding.transactionDetailsTextView
        val date: TextView = binding.transactionDateTextView
        val value: TextView = binding.transactionValueTextView

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }

    }

    companion object {
        private const val TAG = "TransactionListRecyclerViewAdapter"
    }

}