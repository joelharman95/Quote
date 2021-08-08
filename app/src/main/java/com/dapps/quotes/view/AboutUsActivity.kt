package com.dapps.quotes.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.BuildConfig
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_single_quotes.*
import java.util.concurrent.TimeUnit

class AboutUsActivity : AppCompatActivity() {

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        tvVersion.text = "Version: ${BuildConfig.VERSION_CODE}"

        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        PreferenceManager(this).apply {
            if (getExpiryTime() > System.currentTimeMillis()) {
            } else
                clearAds()
            counter = getAdCount()
            if (counter >= 5)
                adView?.visibility = View.INVISIBLE
            else {
                adView?.visibility = View.VISIBLE
                loadAd()
            }
            adView?.resume()
        }
        super.onResume()
    }

    private fun loadAd() {
        MobileAds.initialize(this) { }
        val adRequest: AdRequest = AdManagerAdRequest.Builder().build()
        if (counter <= 4)
            adView?.loadAd(adRequest)
        adView?.adListener = object : AdListener() {
            override fun onAdOpened() {
                counter++
                PreferenceManager(this@AboutUsActivity).apply {
                    saveAdCount(counter)
                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))  //  2 minutes
//                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440))  //  24 hours
                }
                if (counter >= 5)
                    adView?.visibility = View.INVISIBLE
            }
        }
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

}