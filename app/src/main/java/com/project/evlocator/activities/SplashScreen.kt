package com.project.evlocator.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.project.evlocator.R
import com.project.evlocator.SharedPreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : ComponentActivity() {

    lateinit var animationView: LottieAnimationView
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Hide the status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // Set the content view
        setContentView(R.layout.activity_splash_screen)

        animationView = findViewById(R.id.animationView)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        // Perform additional tasks in a coroutine
        lifecycleScope.launch {
            // Simulate a delay for splash screen
            delay(3000)
            animationView.pauseAnimation()
            // Perform any initialization tasks here
            // For example, navigate to the main activity
            val loginStatus = sharedPreferencesManager.getLoginStatus()

            if (loginStatus == "loggedIn") {
                startActivity(Intent(this@SplashScreen, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }


        }
    }
}
