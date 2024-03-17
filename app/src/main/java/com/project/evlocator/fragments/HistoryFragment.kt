package com.project.evlocator.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.adapters.HistoryAdapter
import com.project.evlocator.api.ApiUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HistoryFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.charging_station_rv)

        sharedPreferencesManager = SharedPreferencesManager(view.context)

        context?.let { historyApi(it) }


        return view
    }

    fun historyApi(context: Context) {

        val userId = sharedPreferencesManager.getUserId()
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().orderHistory(userId)
            if (response.isSuccessful) {
                val root = response.body()
                withContext(Dispatchers.Main) {
                    if (root != null) {
                        if (root.status == true) {

                            val historyAdapter = HistoryAdapter(context, root)
                            recyclerView.adapter = historyAdapter


                        } else {
                            Toast.makeText(context, "tryAgain", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }


}