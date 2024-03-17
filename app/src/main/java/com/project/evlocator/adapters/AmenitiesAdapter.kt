package com.project.evlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.evlocator.R
import com.project.evlocator.models.RootX

class AmenitiesAdapter(
    private val context: Context, private val root: RootX
) : RecyclerView.Adapter<AmenitiesAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val amnityName: TextView
        val amnityImg: ImageView

        init {
            amnityName = itemView.findViewById(R.id.amenities_name_tv)
            amnityImg = itemView.findViewById(R.id.amenities_icon_iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_amenities, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.amenitiesDetails?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.amnityName.text = root.amenitiesDetails?.get(position)?.name ?: ""
        Glide.with(context).load(root.amenitiesDetails?.get(position)?.icons).into(holder.amnityImg)
    }

}