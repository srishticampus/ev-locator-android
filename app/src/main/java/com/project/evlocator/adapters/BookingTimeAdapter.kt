package com.project.evlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.models.TimeSlot

class BookingTimeAdapter(
    private val context: Context,
    private val listner: TimeClickListner,
    private val root: TimeSlot
) : RecyclerView.Adapter<BookingTimeAdapter.MyViewHolder>() {
    var selectedPosition = RecyclerView.NO_POSITION

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startTime = itemView.findViewById<TextView>(R.id.start_time_tv)
        val itemLayout = itemView.findViewById<RelativeLayout>(R.id.item_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_booking_time, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.dataAvailable?.get(0)?.timeslot?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.startTime.text =
            root.dataAvailable?.get(0)?.timeslot?.get(position)?.timeSlot.toString()
        holder.itemLayout.setBackgroundResource(
            if (position == selectedPosition) R.drawable.booking_date_bg_seletcted
            else R.drawable.booking_date_bg
        )
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                selectedPosition = position
                notifyDataSetChanged()
                val sharedPreferencesManager = SharedPreferencesManager(context)
                sharedPreferencesManager.saveChoosenTime(
                    root.dataAvailable?.get(0)?.timeslot?.get(
                        position
                    )?.timeSlot.toString()
                )
                listner.onTimeClick(root.dataAvailable?.get(0)?.timeslot?.get(position)?.timeSlot.toString())
            }
        }
    }

}