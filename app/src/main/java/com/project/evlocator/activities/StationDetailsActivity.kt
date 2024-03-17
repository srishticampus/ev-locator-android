package com.project.evlocator.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.adapters.AmenitiesAdapter
import com.project.evlocator.api.ApiUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationDetailsActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var bookNowBtn: TextView
    lateinit var chargerImg: ShapeableImageView
    lateinit var coverImg: ImageView
    lateinit var chargingStationName: TextView
    lateinit var chargingStationAddress: TextView
    lateinit var contactNumber: TextView
    lateinit var locationButton: TextView
    var phoneNumber: String? = null
    var latitude: String? = null
    var longitude: String? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_details)

        recyclerView = findViewById(R.id.amenities_rv)
        chargerImg = findViewById(R.id.charging_station_img_iv)
        chargingStationName = findViewById(R.id.tv_charging_station_name)
        chargingStationAddress = findViewById(R.id.tv_charging_station_address)
        contactNumber = findViewById(R.id.contact_num_tv)
        locationButton = findViewById(R.id.station_location_tv)
        bookNowBtn = findViewById(R.id.book_now_btn_tv)
        coverImg = findViewById(R.id.charging_station_cover_iv)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        contactNumber.setOnClickListener {
            if (phoneNumber.equals(null)) {
                Toast.makeText(applicationContext, "Contact Not Available", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            }

        }

        locationButton.setOnClickListener {
            if (latitude.equals(null) || longitude.equals(null)) {
                Toast.makeText(applicationContext, "Location Not Available", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val location = "$latitude,$longitude?q=$latitude,$longitude"
                val uri = "geo:$location"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }


        }

        bookNowBtn.setOnClickListener {
            val intent = Intent(applicationContext, BookingActivity::class.java)
            startActivity(intent)
        }

        var stationId = sharedPreferencesManager.getStationId()
        stationId = intent.getStringExtra("station_id").toString()
        viewStationDetails(stationId)

    }

    fun viewStationDetails(stationId: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val response =
                ApiUtilities.getInstance().viewStationDetails(stationId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        Glide.with(this@StationDetailsActivity).load(root.stationDetails?.profile)
                            .into(chargerImg)
                        Glide.with(this@StationDetailsActivity)
                            .load(root.stationDetails?.cover_photo)
                            .into(coverImg)
                        chargingStationName.text = root.stationDetails?.name
                        chargingStationAddress.text = root.stationDetails?.address
                        phoneNumber = root.stationDetails?.phone
                        latitude = root.stationDetails?.lattitude
                        longitude = root.stationDetails?.longitude
                        val amenitiesAdapter = AmenitiesAdapter(applicationContext, root)
                        recyclerView.adapter = amenitiesAdapter
                    }

                }
            }


        }
    }

}