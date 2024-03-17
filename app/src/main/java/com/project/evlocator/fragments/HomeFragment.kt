package com.project.evlocator.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.evlocator.R
import com.project.evlocator.activities.SearchActivity
import com.project.evlocator.adapters.ChargingStationAdapter
import com.project.evlocator.api.ApiUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchImg: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.charging_station_rv)
        searchImg = view.findViewById(R.id.search_icon)


        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_DENIED
        ) {
            checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION, 101)
        } else {


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)
            if (ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {


                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    // Toast.makeText(context, "${it.latitude}", Toast.LENGTH_SHORT).show()
                    // viewStations(it.latitude, it.longitude)
                    viewStations(8.5686, 76.8731)

                }
            }


        }

        searchImg.setOnClickListener {

            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)

        }




        return view
    }

    fun viewStations(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {

            val response =
                ApiUtilities.getInstance().viewStations(latitude.toString(), longitude.toString())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    val chargingStationAdapter =
                        root?.let { context?.let { it1 -> ChargingStationAdapter(it1, it) } }
                    recyclerView.adapter = chargingStationAdapter
                }
            }


        }
    }


    fun checkPermissions(permission: String, requestCode: Int) {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                )
            } == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf<String>(permission),
                requestCode
            )
        } else {
            Toast.makeText(
                context,
                "Permission Already granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}