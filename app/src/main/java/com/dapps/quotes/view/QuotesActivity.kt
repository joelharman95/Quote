package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.dapps.quotes.utils.Constants
import com.dapps.quotes.utils.hideKeyboard
import com.dapps.quotes.utils.openBrowser
import com.dapps.quotes.utils.openMail
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.btnBackToTop
import kotlinx.android.synthetic.main.activity_quotes.*
import kotlinx.android.synthetic.main.activity_quotes.adView
import kotlinx.android.synthetic.main.activity_quotes.drawerLayout
import kotlinx.android.synthetic.main.activity_quotes.etSearch
import kotlinx.android.synthetic.main.activity_quotes.ivMenu
import kotlinx.android.synthetic.main.activity_quotes.ivSearch
import kotlinx.android.synthetic.main.activity_quotes.tvCount
import kotlinx.android.synthetic.main.activity_quotes.tvQuote
import java.util.*
import java.util.concurrent.TimeUnit

class QuotesActivity : AppCompatActivity() {
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        val myCollection =
            Gson().fromJson(intent.getStringExtra(Constants.COLLECTION), MyCollection::class.java)
        val myQuotesList =
            if (myCollection.quotes.isNullOrEmpty()) listOf() else myCollection.quotes
        tvQuote.text = myCollection.title
        tvCount.text = "${myCollection.count} Quotes"

        ivMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
        }
        val filteredList = mutableListOf<Quotes>()
        filteredList.addAll(myQuotesList)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.privacyPolicy -> openBrowser()
                R.id.contactUs -> openMail()
                R.id.aboutUs -> startActivity(Intent(this, AboutUsActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
        ivSearch.setOnClickListener {
            filteredList.clear()
            filteredList.addAll(myQuotesList.filter { quote ->
                (quote.quote + " " + quote.author).toLowerCase(Locale.getDefault()).contains(
                    etSearch.text.toString()
                )
            })
            (rvQuotes.adapter as QuotesAdapter).setQuotesList(filteredList)
            it.hideKeyboard()
        }
        rvQuotes.adapter = QuotesAdapter { myQuote, position ->
            val intent = Intent(this, SingleQuotesActivity::class.java)
            intent.putExtra(Constants.QUOTES, Gson().toJson(filteredList))
            intent.putExtra(Constants.COLLECTION_COUNT, filteredList.size)
            intent.putExtra(Constants.POSITION, position)
            startActivity(intent)
        }
        rvQuotes.requestFocus()
        (rvQuotes.adapter as QuotesAdapter).setQuotesList(myQuotesList)
        btnBackToTop.visibility = View.GONE

        btnBackToTop.setOnClickListener {
            rvQuotes.scrollToPosition(0)
            btnBackToTop.visibility = View.GONE
        }

        rvQuotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition > 0) {
                    btnBackToTop.visibility = View.VISIBLE
                } else {
                    btnBackToTop.visibility = View.GONE
                }
            }
        })
    }

    override fun onResume() {
        PreferenceManager(this).apply {
            if (getExpiryTime() > System.currentTimeMillis()) {
            } else
                clearAds()
            counter = getAdCount()
            if (counter >= 5)
                adView?.visibility = View.GONE
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
                PreferenceManager(this@QuotesActivity).apply {
                    saveAdCount(counter)
                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))  //  2 minutes
//                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440))  //  24 hours
                }
                if (counter >= 5)
                    adView.visibility = View.GONE
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}