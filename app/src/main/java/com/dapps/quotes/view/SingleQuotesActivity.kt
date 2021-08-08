package com.dapps.quotes.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.dapps.quotes.utils.Constants
import com.dapps.quotes.utils.Constants.POSITION
import com.dapps.quotes.utils.applySpanPo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quotes.*
import kotlinx.android.synthetic.main.activity_quotes.tvCount
import kotlinx.android.synthetic.main.activity_quotes.tvQuote
import kotlinx.android.synthetic.main.activity_single_quotes.*
import kotlinx.android.synthetic.main.activity_single_quotes.adView
import java.util.concurrent.TimeUnit

class SingleQuotesActivity : AppCompatActivity() {

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_quotes)

        val type = object : TypeToken<List<Quotes>>() {}.type
        val myQuotesList: MutableList<Quotes> =
            Gson().fromJson(intent.getStringExtra(Constants.QUOTES), type)
        setQuotes(myQuotesList[intent.getIntExtra(POSITION, 0)], intent.getIntExtra(POSITION, 0))

        ivLeft.setOnClickListener {
            if ((tvCount.tag as Int) == 0) {
                setQuotes(myQuotesList[myQuotesList.size - 1], myQuotesList.size - 1)
            } else if ((tvCount.tag as Int) <= myQuotesList.size - 1) {
                setQuotes(myQuotesList[(tvCount.tag as Int) - 1], (tvCount.tag as Int) - 1)
            }
        }
        ivRight.setOnClickListener {
            if ((tvCount.tag as Int) >= 0 && (tvCount.tag as Int) < myQuotesList.size - 1) {
                setQuotes(myQuotesList[(tvCount.tag as Int) + 1], (tvCount.tag as Int) + 1)
            } else if ((tvCount.tag as Int) == myQuotesList.size - 1) {
                setQuotes(myQuotesList[0], 0)
            }
        }

        btnCopy.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData =
                ClipData.newPlainText("quote", "${tvQuote.text}\n${tvAuthor.text}")
//            val clipData: ClipData = ClipData.newPlainText("quote", "${tvQuote.text}")
            clipboardManager.setPrimaryClip(clipData)
//            btnCopy.background = AppCompatResources.getDrawable(baseContext, R.drawable.bg_copied)
            btnCopy.setBackgroundColor(getColor(R.color.card_bg))
            btnCopy.setTextColor(Color.parseColor("#000000"))
            btnCopy.strokeColor = ColorStateList.valueOf(getColor(R.color.light_gray))
            btnCopy.text = "Copied!!!"
        }

    }

    private fun setQuotes(myQuote: Quotes, position: Int) {
//        btnCopy.background = AppCompatResources.getDrawable(baseContext, R.drawable.search_view_border)
        btnCopy.setBackgroundColor(getColor(R.color.white))
        btnCopy.strokeColor = ColorStateList.valueOf(getColor(R.color.light_gray))
        btnCopy.text = getString(R.string.action_copy_quotes)
        btnCopy.setTextColor(getColor(R.color.black))
        tvCount.applySpanPo(
            "${position + 1}",
            R.color.black,
            " Of ${intent.getIntExtra(Constants.COLLECTION_COUNT, 0)}"
        )
        tvCount.tag = position
//        tvQuote.text = "\"${myQuote.quote}\"\n${myQuote.author}"
        tvQuote.text = "\"${myQuote.quote}\""
        tvAuthor.text = myQuote.author
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
            adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdOpened() {
                counter++
                PreferenceManager(this@SingleQuotesActivity).apply {
                    saveAdCount(counter)
                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))  //  2 minutes
//                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440))  //  24 hours
                }
                if (counter >= 5)
                    adView.visibility = View.INVISIBLE
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