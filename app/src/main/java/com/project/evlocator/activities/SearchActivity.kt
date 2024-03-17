package com.project.evlocator.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.evlocator.adapters.ChargingStationAdapter
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBt.setOnClickListener {
            if (binding.searchEt.equals(null)) {
                Toast.makeText(applicationContext, "Enter Something", Toast.LENGTH_SHORT).show()
            } else {
                searchApi()
            }
        }


    }

    fun searchApi() {

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiUtilities.getInstance().searchStation(binding.searchEt.text.toString())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {
                            val chargingStationAdapter =
                                ChargingStationAdapter(applicationContext, root)
                            binding.searchRv.adapter = chargingStationAdapter

                        } else {
                            Toast.makeText(applicationContext, root.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}