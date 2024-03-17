package com.project.evlocator.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import com.project.evlocator.R
import com.project.evlocator.fragments.HistoryFragment
import com.project.evlocator.fragments.HomeFragment
import com.project.evlocator.fragments.ProfileFragment
import me.ibrahimsn.lib.SmoothBottomBar

class HomeActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    lateinit var bottomNav: SmoothBottomBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNav = findViewById(R.id.bottomNav)
        loadFragment(HomeFragment())

        bottomNav.onItemSelected = {

            if (it == 0) {
                loadFragment(HomeFragment())
                true
            } else if (it == 1) {
                loadFragment(HistoryFragment())
                true
            } else if (it == 2) {
                loadFragment(ProfileFragment())
                true
            }
            false
        }




    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}