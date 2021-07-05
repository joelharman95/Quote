package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        PreferenceManager(this).apply {
            //  TODO  ::  ::  Need to add logic to show quotes daily based on date saved on pref
            tvQuote.text = "Whatever you do, \ndo it well."
            tvAuthor.text = "- Walt Disney"
        }

        GlobalScope.launch(Dispatchers.Main) {  //  Working on main thread
//            delay(2000)
            finish()
            startActivity(Intent(this@Splash, MainActivity::class.java))
        }

    }
}