package com.project.evlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.activities.OrderHistoryActivity
import com.project.evlocator.models.OrderHistory

class HistoryAdapter(
    private val context: Context, private val root: OrderHistory
) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val stationIv = itemView.findViewById<ShapeableImageView>(R.id.iv_charging_station)
        val stationNameTv = itemView.findViewById<TextView>(R.id.tv_charging_station_name)
        val chargingDate = itemView.findViewById<TextView>(R.id.tv_charging_date)
        val chargingStatus = itemView.findViewById<TextView>(R.id.tv_num_of_chargers)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_history, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.stationDetails?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        Glide.with(context).load(root.stationDetails?.get(position)?.file).into(holder.stationIv)
        holder.stationNameTv.text = root.stationDetails?.get(position)?.name
        holder.chargingDate.text = root.stationDetails?.get(position)?.booking_date
        holder.chargingStatus.text = root.stationDetails?.get(position)?.charging_status

        holder.itemView.setOnClickListener {
            val intent = Intent(context, OrderHistoryActivity::class.java)
            val sharedPreferencesManager = SharedPreferencesManager(context)
            sharedPreferencesManager.saveBookingId(
                root.stationDetails?.get(position)?.id
                    .toString()
            )
            context.startActivity(intent)
        }


    }

}