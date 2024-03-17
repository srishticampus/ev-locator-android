package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.Utils.PUBLISHABLE_KEY
import com.project.evlocator.adapters.BookingDateAdapter
import com.project.evlocator.adapters.BookingTimeAdapter
import com.project.evlocator.adapters.ChargerClickListner
import com.project.evlocator.adapters.ChargersAdapter
import com.project.evlocator.adapters.DateClickListner
import com.project.evlocator.adapters.TimeClickListner
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.models.MyDate
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity(), DateClickListner, ChargerClickListner,
    TimeClickListner {
    private lateinit var dateRv: RecyclerView
    private lateinit var chargersRv: RecyclerView
    private lateinit var timeRv: RecyclerView
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerId: String
    lateinit var ephemeralKey: String
    lateinit var clientSecert: String
    lateinit var payNowBtn: TextView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        dateRv = findViewById(R.id.date_rv)
        chargersRv = findViewById(R.id.chargers_rv)
        timeRv = findViewById(R.id.time_rv)
        payNowBtn = findViewById(R.id.pay_now_btn_tv)

        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)


        bookingDateApi()



        getCustomerPayment()
        payNowBtn.setOnClickListener {
            paymentFlow()
        }

    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecert,
            PaymentSheet.Configuration(
                "Ev locator",
                PaymentSheet.CustomerConfiguration(
                    customerId, ephemeralKey
                )
            )
        )

    }

    private var apiInterface = ApiUtilities.getPaymentApiInterface()

    private fun getCustomerPayment() {
        lifecycleScope.launch(Dispatchers.IO) {
            val res = apiInterface.getCustomer()
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {
                    customerId = res.body()!!.id
                    getEphemeralKey(customerId)
                }
            }
        }
    }

    private fun getEphemeralKey(customerId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val res = apiInterface.getEphemeralKey(customerId)
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {
                    ephemeralKey = res.body()!!.id
                    getPaymentIntent(customerId, ephemeralKey)
                }
            }
        }
    }

    private fun getPaymentIntent(customerId: String, ephemeralKey: String) {


        lifecycleScope.launch(Dispatchers.IO) {
            val res = apiInterface.getPaymentIntent(customerId, "5000")
            withContext(Dispatchers.Main) {

                if (res.isSuccessful && res.body() != null) {
                    clientSecert = res.body()!!.client_secret
                    Toast.makeText(
                        this@BookingActivity,
                        "Proceed for payment", Toast.LENGTH_SHORT
                    ).show()


                }
            }
        }
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment done", Toast.LENGTH_SHORT).show()
            bookingApi()
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }


    fun bookingDateApi() {

        getDates()
        val bookingDateAdapter = BookingDateAdapter(applicationContext, getDates(), this)
        dateRv.adapter = bookingDateAdapter
    }

    fun getDates(): List<MyDate> {
        val dates = mutableListOf<MyDate>()
        val calendar = Calendar.getInstance()

        // Format for month in three letters (e.g., Nov)
        val monthFormat = SimpleDateFormat("MMM", Locale.US)
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.US)

        for (i in 0 until 6) { // Get today and the next 5 days
            val currentDate = calendar.time
            val month = monthFormat.format(currentDate)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = dayOfWeekFormat.format(currentDate)
            val year = calendar.get(Calendar.YEAR)

            val myDate = MyDate(month, day, dayOfWeek, year.toString())
            dates.add(myDate)

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    override fun onDateItemClick(clickedDate: String) {

        Log.i("date", clickedDate)
        chargersListApi(clickedDate)

    }

    fun chargersListApi(selectedDate: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiUtilities.getInstance().availableChargers(
                    sharedPreferencesManager.getStationId().toString(),
                    selectedDate
                )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        val chargersAdapter =
                            ChargersAdapter(applicationContext, root, this@BookingActivity)
                        chargersRv.adapter = chargersAdapter
                    } else {
                        chargersRv.visibility = View.GONE
                    }
                }
            }
        }

    }

    override fun onChargerItemClick(chargerName: String) {

        Log.i("chargerId", chargerName)
        availableTimeSlotApi(chargerName)

    }

    fun availableTimeSlotApi(chargerName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().availableTimeSlot(
                chargerName,
                sharedPreferencesManager.getStationId(),
                sharedPreferencesManager.getChosenDate()
            )
            withContext(Dispatchers.Main) {
                val root = response.body()
                if (root != null) {
                    val bookingTimeAdapter =
                        BookingTimeAdapter(applicationContext, this@BookingActivity, root)
                    timeRv.adapter = bookingTimeAdapter
                }
            }

        }
    }

    override fun onTimeClick(selectedTime: String) {

        Log.i("time", sharedPreferencesManager.getChoosenTime().toString())

    }

    private fun bookingApi() {
        val userId = sharedPreferencesManager.getUserId()
        val stationId = sharedPreferencesManager.getStationId()
        val date = sharedPreferencesManager.getChosenDate()
        val charger = sharedPreferencesManager.getChoosenCharegr()
        val timeSlot = sharedPreferencesManager.getChoosenTime()

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiUtilities.getInstance().bookingApi(userId, stationId, date, charger, timeSlot)
            withContext(Dispatchers.Main) {
                val root = response.body()
                if (root != null) {
                    if (root.status == true){
                        Toast.makeText(applicationContext, "Booking Done", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(applicationContext, "Booking Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Booking Failed", Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


}