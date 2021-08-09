package com.dapps.quotes.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.BuildConfig
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.dapps.quotes.utils.showToast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.activity_about_us.*
import java.util.concurrent.TimeUnit

class AboutUsActivity : AppCompatActivity() {

    private var nativeAd: NativeAd? = null
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
                flAd?.visibility = View.GONE
            else {
                flAd?.visibility = View.VISIBLE
                loadAd()
            }
        }
        super.onResume()
    }

    private fun loadAd() {
        MobileAds.initialize(this) { }
        val builder =
            AdLoader.Builder(this, resources.getString(R.string.NATIVE_ADS_ADVANCED_UNIT_ID))
        builder.forNativeAd { nativeAd: NativeAd ->
            if (isDestroyed || isFinishing || isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            this.nativeAd?.destroy()
            this.nativeAd = nativeAd
            val adView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            flAd.removeAllViews()
            flAd.addView(adView)
        }
        builder.withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain,
                        loadAdError.code,
                        loadAdError.message
                    )
                    showToast("Failed to load native ad with error $error")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    counter++
                    PreferenceManager(this@AboutUsActivity).apply {
                        saveAdCount(counter)
                        saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))  //  2 minutes
//                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440))  //  24 hours
                    }
                    if (counter >= 5)
                        flAd?.visibility = View.INVISIBLE
                }
            })

        if (counter <= 4) {
            val adLoader = builder.build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)
        if (nativeAd.body == null)
            adView.bodyView.visibility = View.INVISIBLE
        else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null)
            adView.callToActionView.visibility = View.INVISIBLE
        else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null)
            adView.iconView.visibility = View.GONE
        else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null)
            adView.priceView.visibility = View.INVISIBLE
        else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null)
            adView.storeView.visibility = View.INVISIBLE
        else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null)
            adView.starRatingView.visibility = View.INVISIBLE
        else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null)
            adView.advertiserView.visibility = View.INVISIBLE
        else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }

}