package com.project.evlocator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.models.Faq

class FaqAdapter(
    private val context: Context, private val root: Faq
) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val questionTv: TextView
        val ansTv: TextView

        init {
            questionTv = itemView.findViewById(R.id.question_tv)
            ansTv = itemView.findViewById(R.id.ans_tv)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_faq_layout, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return root.stationDetails!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.questionTv.text = root.stationDetails?.get(position)?.question
        holder.ansTv.text = root.stationDetails?.get(position)?.answers

    }

}