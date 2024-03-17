package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.Utils
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.databinding.ActivityOrderHistoryBinding
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerId: String
    lateinit var ephemeralKey: String
    lateinit var clientSecert: String
    var balanceAmoun: String = "5000"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        PaymentConfiguration.init(this, Utils.PUBLISHABLE_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        // getCustomerPayment()


        historyDetailsApi()
        binding.bookNowBtnTv.setOnClickListener {
            if (binding.bookNowBtnTv.text.equals("START CHARGING")) {
                startCharging()
            } else if (binding.bookNowBtnTv.text.equals("STOP CHARGING")) {
                stopCharging()
            } else if (binding.bookNowBtnTv.text.equals("PAY NOW")) {
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
                // paymentFlow()
            }
        }

        binding.ratingSubmitBtn.setOnClickListener {
            if (binding.ratingBar.rating.toString().isNotEmpty() && binding.userReviewEt.text.isNotEmpty()) {
                addRating()
            } else {
                Toast.makeText(applicationContext, "Add Rating", Toast.LENGTH_SHORT).show()
            }
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
                        this@OrderHistoryActivity,
                        "Proceed for payment", Toast.LENGTH_SHORT
                    ).show()


                }
            }
        }
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment done", Toast.LENGTH_SHORT).show()
            balancePayApi()
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }


    fun historyDetailsApi() {
        val bookingId = sharedPreferencesManager.getBookingId()
        val userId = sharedPreferencesManager.getUserId()

        binding.progressLayout.visibility = View.VISIBLE


        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().detailedOrderHistory(userId, bookingId)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {

                            binding.progressLayout.visibility = View.GONE


                            binding.bookingIdTv.text = root.stationDetails?.get(0)?.id.toString()
                            binding.tvChargingStationName.text =
                                root.stationDetails?.get(0)?.name.toString()
                            binding.tvChargingStationAddress.text =
                                root.stationDetails?.get(0)?.address.toString()
                            binding.choosenCharger.text =
                                root.stationDetails?.get(0)?.charger_name.toString()
                            binding.date.text = root.stationDetails?.get(0)?.booking_date.toString()
                            binding.bookingTime.text =
                                root.stationDetails?.get(0)?.timeslot.toString()
                            binding.statusTv.text =
                                root.stationDetails?.get(0)?.charging_status.toString()

                            if (root.stationDetails?.get(0)?.booking_status.equals("1")) {
                                if (root.stationDetails?.get(0)?.charging_status.toString()
                                        .equals("Upcoming")
                                ) {
                                    binding.bookNowBtnTv.visibility = View.VISIBLE
                                    binding.bookNowBtnTv.text = "START CHARGING"
                                } else if (root.stationDetails?.get(0)?.charging_status.toString()
                                        .equals("Ongoing")
                                ) {
                                    binding.bookNowBtnTv.visibility = View.VISIBLE
                                    binding.bookNowBtnTv.text = "STOP CHARGING"
                                } else if (root.stationDetails?.get(0)?.charging_status.toString()
                                        .equals("Completed")
                                ) {
                                    paymentDetailsApi()
                                    //getCustomerPayment()
                                    binding.bookNowBtnTv.visibility = View.VISIBLE
                                    binding.bookNowBtnTv.text = "PAY NOW"
                                } else if (root.stationDetails?.get(0)?.charging_status.toString()
                                        .equals("Payment Completed")
                                ) {
                                    paymentDetailsApi()
                                    binding.howTxt.visibility = View.VISIBLE
                                    binding.ratingBar.visibility = View.VISIBLE
                                    binding.userReviewEt.visibility = View.VISIBLE
                                    binding.ratingSubmitBtn.visibility = View.VISIBLE
                                }
                            } else {
                                //  Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
                            }


                        } else {
                            Toast.makeText(applicationContext, "Try Again", Toast.LENGTH_SHORT)
                                .show()
                            binding.progressLayout.visibility = View.GONE

                        }
                    } else {
                        Toast.makeText(applicationContext, "Try Again", Toast.LENGTH_SHORT).show()
                        binding.progressLayout.visibility = View.GONE

                    }
                }
            }
        }

    }

    fun startCharging() {
        val bookingId = sharedPreferencesManager.getBookingId()
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.i("timeee", currentTime.toString())
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().startCharging(bookingId, currentTime)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {
                            Toast.makeText(
                                applicationContext,
                                "Charging Started",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.bookNowBtnTv.visibility = View.VISIBLE
                            binding.bookNowBtnTv.text = "STOP CHARGING"

                        } else {
                            Toast.makeText(
                                applicationContext,
                                root.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    fun stopCharging() {
        val bookingId = sharedPreferencesManager.getBookingId()
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.i("timeee", currentTime.toString())
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().stopCharging(bookingId, currentTime)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {
                            Toast.makeText(
                                applicationContext,
                                "Charging Stopped",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.bookNowBtnTv.visibility = View.VISIBLE
                            binding.bookNowBtnTv.text = "PAY NOW"
                            paymentDetailsApi()
                        }
                    }
                }
            }
        }
    }

    fun paymentDetailsApi() {
        val bookingId = sharedPreferencesManager.getBookingId()
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().paymentDetails(bookingId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {
                            balanceAmoun = root.paymentDetails?.get(0)?.pending_amount.toString()
                            binding.finalAmount.text =
                                root.paymentDetails?.get(0)?.final_amount.toString()
                            binding.pendingAmount.text =
                                root.paymentDetails?.get(0)?.pending_amount.toString()
                        } else {

                        }
                    }
                }
            }

        }
    }

    fun balancePayApi() {
        val bookingId = sharedPreferencesManager.getBookingId()
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().balancePay(bookingId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root != null) {
                        if (root.status == true) {
                            Toast.makeText(applicationContext, "Paid", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    fun addRating() {
        val bookingId = sharedPreferencesManager.getBookingId()
        val userId = sharedPreferencesManager.getUserId()
        val stationId = sharedPreferencesManager.getStationId()
        val starCount = binding.ratingBar.rating.toString()
        val review = binding.userReviewEt.text.toString()
        val params = HashMap<String?, String?>()
        params["count"] = starCount
        params["station_id"] = stationId
        params["user_id"] = userId
        params["booking_id"] = bookingId
        params["review"] = review

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiUtilities.getInstance().addRating(params)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root?.status == true) {
                        Toast.makeText(applicationContext, root.message, Toast.LENGTH_SHORT).show()
                        binding.howTxt.visibility = View.GONE
                        binding.ratingBar.visibility = View.GONE
                        binding.userReviewEt.visibility = View.GONE
                        binding.ratingSubmitBtn.visibility = View.GONE
                    } else {
                        Toast.makeText(applicationContext, root?.message.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }


    }
}