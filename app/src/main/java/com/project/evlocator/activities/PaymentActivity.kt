package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.databinding.ActivityPaymentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        binding.payNowBt.setOnClickListener {

            if (binding.cardNumberEt.text.isNotEmpty() && binding.expiryDate.text.isNotEmpty() && binding.cvv.text.isNotEmpty() && binding.outlinedTextFieldFour.text.isNotEmpty()) {
                balancePayApi()

            } else {
                Toast.makeText(applicationContext, "Fill The Details", Toast.LENGTH_SHORT).show()
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
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(applicationContext, "Paid", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Payment Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }
    }
}