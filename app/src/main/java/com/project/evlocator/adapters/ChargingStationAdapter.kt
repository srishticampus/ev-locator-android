package com.project.evlocator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.activities.StationDetailsActivity
import com.project.evlocator.models.Root

class ChargingStationAdapter(
    private val context: Context, private val root: Root
) : RecyclerView.Adapter<ChargingStationAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val chargerImg: ImageView
        val chargingStationName: TextView
        val chargingStationAddress: TextView
        val numberOfChargeres: TextView
        val ratingBar: AppCompatRatingBar

        init {
            chargerImg = itemView.findViewById(R.id.iv_charging_station)
            chargingStationName = itemView.findViewById(R.id.tv_charging_station_name)
            chargingStationAddress = itemView.findViewById(R.id.tv_charging_station_adress)
            numberOfChargeres = itemView.findViewById(R.id.tv_num_of_chargers)
            ratingBar = itemView.findViewById(R.id.ratingbarr)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_home_charging_station, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.stationDetails!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(root.stationDetails?.get(position)?.file).into(holder.chargerImg)
        holder.chargingStationName.text = root.stationDetails!![position]!!.name
        holder.chargingStationAddress.text = root.stationDetails!![position]!!.address
        holder.numberOfChargeres.text =
            "${root.stationDetails!![position]!!.no_of_charges_available.toString()} Ports Available"
        try {
            val rating = root.stationDetails!![position]!!.rating_count.toString()
            holder.ratingBar.rating = rating.toFloat()
            // holder.ratingBar.rating = 2.0f
        } catch (e: Exception) {
            //        Toast.makeText(context, root.stationDetails!![position]!!.ratingCount.toString(), Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, StationDetailsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            var sharedPreferencesManager = SharedPreferencesManager(context)
            sharedPreferencesManager.saveStationId(root.stationDetails!![position]?.id.toString())
            intent.putExtra("station_id", root.stationDetails!![position]?.id)
            context.startActivity(intent)
        }

    }

}