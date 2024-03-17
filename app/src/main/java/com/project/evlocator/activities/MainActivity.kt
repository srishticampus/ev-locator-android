package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.project.evlocator.SharedPreferencesManager
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var deviceToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        installSplashScreen()
        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        binding.signUpBt.setOnClickListener {
            intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }

        // deviceToken = "hello"
//get deviceToken
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            deviceToken = it.result //this is the token retrieved
            Log.d("TAG", deviceToken)

        }
        binding.loginBtn.setOnClickListener {

            if (checkAllFields()) {
                lifecycleScope.launch {
                    try {
                        val api = ApiUtilities.getInstance()
                        val result = api.userLogin(
                            binding.etPhoneNumber.text.toString(),
                            binding.etPassword.text.toString(), deviceToken
                        )
                        result.body()?.let { root ->
                            if (root.status) {
                                intent = Intent(applicationContext, HomeActivity::class.java)
                                sharedPreferencesManager.saveUserId(root.userData[0].id)
                                sharedPreferencesManager.savePhoneNumber(root.userData[0].phone)
                                sharedPreferencesManager.saveLoginStatus("loggedIn")
                                startActivity(intent)
                            } else {
                                Toast.makeText(applicationContext, root.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } ?: Toast.makeText(
                            applicationContext,
                            "Server Error: ${result.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Connect to Internet and try again",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

    private fun checkAllFields(): Boolean {
        var isValid = true

        // Phone
        if (binding.etPhoneNumber.length() == 0) {
            binding.etPhoneNumber.error = "Phone number is required"
            isValid = false
        }
        // Password
        if (binding.etPassword.length() == 0) {
            binding.etPassword.error = "Password is required"
            isValid = false
        } else if (binding.etPassword.length() < 8) {
            binding.etPassword.error = "Password must be minimum 8 characters"
            isValid = false
        }
        return isValid

    }
}