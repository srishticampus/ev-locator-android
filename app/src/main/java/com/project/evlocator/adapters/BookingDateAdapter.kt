package com.project.evlocator.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.models.MyDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingDateAdapter(
    private val context: Context,
    private val dates: List<MyDate>,
    private val listner: DateClickListner
) : RecyclerView.Adapter<BookingDateAdapter.MyViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val monthTv = itemView.findViewById<TextView>(R.id.month_tv)
        val dateTv = itemView.findViewById<TextView>(R.id.date_tv)
        val dayTv = itemView.findViewById<TextView>(R.id.day_tv)
        val itemLayout = itemView.findViewById<RelativeLayout>(R.id.item_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_booking_date, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.monthTv.text = dates[position].month
        holder.dateTv.text = dates[position].day.toString()
        holder.dayTv.text = dates[position].dayOfWeek
        val year = dates[position].Year


        val currentItem = dates[position]

        holder.itemLayout.setBackgroundResource(
            if (position == selectedPosition) R.drawable.booking_date_bg_seletcted
            else R.drawable.booking_date_bg
        )

        // Handle item click
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {

                // Update selected position
                selectedPosition = position
                val selectedDateString: String =
                    "${dates[position].month},${dates[position].day.toString()},${year}"
                val isoDate = convertToISODate(selectedDateString)
                val sharedPreferencesManager = SharedPreferencesManager(context)
                sharedPreferencesManager.saveChosenDate(isoDate)
//                Log.i("date", isoDate)
//                Log.i("date", sharedPreferencesManager.getChosenDate())
//                Log.i("date", sharedPreferencesManager.getStationId())
                // Notify adapter about the change
                notifyDataSetChanged()
                listner.onDateItemClick(isoDate)


            }
        }


    }

    fun convertToISODate(dateString: String): String {
        val inputFormat = SimpleDateFormat("MMM,dd,yy", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val date: Date = inputFormat.parse(dateString) ?: Date()
        return outputFormat.format(date)
    }


}