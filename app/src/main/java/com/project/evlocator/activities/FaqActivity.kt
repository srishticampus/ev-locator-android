package com.project.evlocator.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.evlocator.R
import com.project.evlocator.adapters.FaqAdapter
import com.project.evlocator.api.ApiUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaqActivity : AppCompatActivity() {


    lateinit var faqRv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faq)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        faqRv = findViewById(R.id.faq_rv)
        apiCall()

    }

    private fun apiCall() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiUtilities.getInstance().faqApi()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val root = response.body()
                    if (root?.status == true) {

                        val faqAdapter = FaqAdapter(applicationContext, root)
                        faqRv.adapter = faqAdapter

                    } else {

                    }
                }
            }
        }
    }

}