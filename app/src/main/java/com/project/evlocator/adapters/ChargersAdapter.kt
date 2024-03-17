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
import com.project.evlocator.models.RootXX

class ChargersAdapter(
    private val context: Context, private val root: RootXX,
    private val listner: ChargerClickListner
) : RecyclerView.Adapter<ChargersAdapter.MyViewHolder>() {
    var selectedPosition = RecyclerView.NO_POSITION


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chargerNameTv = itemView.findViewById<TextView>(R.id.charger_name_tv)
        val itemLayout = itemView.findViewById<RelativeLayout>(R.id.item_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_booking_chargers, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.dataAvailable?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.chargerNameTv.text = root.dataAvailable!![position]?.charger_name
        holder.itemLayout.setBackgroundResource(
            if (position == selectedPosition) R.drawable.booking_date_bg_seletcted
            else R.drawable.booking_date_bg
        )

        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                selectedPosition = position
                notifyDataSetChanged()
                val sharedPreferencesManager = SharedPreferencesManager(context)
                sharedPreferencesManager.saveChoosenCharger(root.dataAvailable!![selectedPosition]?.charger_name.toString())
                listner.onChargerItemClick(root.dataAvailable!![selectedPosition]?.charger_name.toString())
            }
        }

    }

}