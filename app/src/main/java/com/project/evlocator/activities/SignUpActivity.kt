package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.evlocator.api.ApiUtilities
import com.project.evlocator.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userApi = ApiUtilities.getInstance()

        binding.signUpBtn.setOnClickListener {

            if (checkAllFields()) {

                lifecycleScope.launch {

                    try {
                        val result = userApi.userReg(
                            binding.nameEt.text.toString(),
                            binding.phoneNumberEt.text.toString(),
                            binding.emailEt.text.toString(),
                            binding.passwordEt.text.toString()
                        )

                        result.body()?.let { root ->

                            Toast.makeText(applicationContext, root.message, Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)

                        } ?: Toast.makeText(
                            applicationContext,
                            "Server Error: ${result.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Server Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }

            }

        }


    }

    private fun checkAllFields(): Boolean {

        var isValid = true

        // Reset errors
        binding.nameEt.error = null
        binding.phoneNumberEt.error = null
        binding.emailEt.error = null
        binding.passwordEt.error = null
        binding.retypePasswordEt.error = null

        // Name
        if (binding.nameEt.length() == 0) {
            binding.nameEt.error = "Name is required"
            isValid = false
        }

        // Phone
        if (binding.phoneNumberEt.length() == 0) {
            binding.phoneNumberEt.error = "Phone number is required"
            isValid = false
        }

        // Email
        if (binding.emailEt.length() == 0) {
            binding.emailEt.error = "Email is required"
            isValid = false
        } else if (!isEmailValid(binding.emailEt.text.toString())) {
            binding.emailEt.error = "Invalid email address"
            isValid = false
        }

        // Password
        if (binding.passwordEt.length() == 0) {
            binding.passwordEt.error = "Password is required"
            isValid = false
        } else if (binding.passwordEt.length() < 8) {
            binding.passwordEt.error = "Password must be minimum 8 characters"
            isValid = false
        }

        // Retype Password
        if (binding.retypePasswordEt.length() == 0) {
            binding.retypePasswordEt.error = "Retype password is required"
            isValid = false
        } else if (binding.retypePasswordEt.length() < 8) {
            binding.retypePasswordEt.error = "Retype password must be minimum 8 characters"
            isValid = false
        }

        // Check if passwords match
        if (binding.passwordEt.text.toString() != binding.retypePasswordEt.text.toString()) {
            binding.retypePasswordEt.error = "Passwords should be the same"
            isValid = false
        }

        return isValid
    }

    fun isEmailValid(email: CharSequence?): Boolean {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}