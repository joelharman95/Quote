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
            tvQuote.text = getQuotes()
            tvAuthor.text = getAuthor()
        }

        GlobalScope.launch(Dispatchers.Main) {  //  Working on main thread
            delay(2000)
            startActivity(Intent(this@Splash, MainActivity::class.java))
            this@Splash.finish()
        }

    }
}