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
import com.dapps.quotes.utils.*
import com.dapps.quotes.utils.Constants.COLLECTION
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myCollectionList = mutableListOf<MyCollection>()
        val type = object : TypeToken<List<MyCollection>>() {}.type
        myCollectionList.addAll(Gson().fromJson(getQuotes(), type))

        val count = myCollectionList.map { it.count.toInt() }.sum()
        tvCount.text = "${myCollectionList.size} Categories, $count Quotes"

        PreferenceManager(this).apply {
            if (getDate().isNotEmpty()) {
                val savedDate =
                    getDate().getDateFrom("EEE MMM dd HH:mm:ss zzz yyyy", "yyyy.MM.dd HH:mm")
                val currentDate = Date(System.currentTimeMillis()).toString()
                    .getDateFrom("EEE MMM dd HH:mm:ss zzz yyyy", "yyyy.MM.dd HH:mm")
                val diff: Long = (currentDate - savedDate)
                val diffSeconds: Long = diff / 1000
                val diffMinutes: Long = diff / (60 * 1000)
                val diffHours: Long = diff / (60 * 60 * 1000)
                val diffDays: Long = diff / (24 * 60 * 60 * 1000)
                println("GET__________ $currentDate ________ $savedDate _______ $diffDays")
                if (diffDays >= 1) {
                    var saved = false
                    for (collection in myCollectionList) {
                        if (saved)
                            break
                        for ((index, quotes) in collection.quotes.withIndex()) {
                            if (quotes.quote.contentEquals(getQuotes())) {
                                if (index + 1 < collection.quotes.size) {
                                    saveQuotes(collection.quotes[index + 1].quote)
                                    saveAuthor(collection.quotes[index + 1].author)
                                    saved = true
                                    break
                                }
                            }
                        }
                    }
                    if (!saved)
                        initialQuote(myCollectionList)
                    saveDate(Date(System.currentTimeMillis()).toString())
                }
            } else {
                initialQuote(myCollectionList)
            }

            tvDailyQuotes.text = getQuotes() + "\n- " + getAuthor()

            if (getExpiryTime() > System.currentTimeMillis()) {
            } else
                clearAds()
            counter = getAdCount()

            if (counter <= 4)
                loadAd()
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
        }
        ivMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        ivShare.setOnClickListener {
            shareApp()
        }
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
            (rvCollection.adapter as CollectionAdapter).setCollectionList(myCollectionList.filter { collection ->
                collection.title.toLowerCase(Locale.getDefault()).contains(
                    etSearch.text.toString().toLowerCase(Locale.getDefault())
                )
            })
            it.hideKeyboard()
        }
        rvCollection.adapter =
            CollectionAdapter { myCollection ->
                val intent = Intent(this, QuotesActivity::class.java)
                intent.putExtra(COLLECTION, Gson().toJson(myCollection))
                startActivity(intent)
            }
        rvCollection.requestFocus()
        (rvCollection.adapter as CollectionAdapter).setCollectionList(myCollectionList)
        btnBackToTop.visibility = View.GONE

        btnBackToTop.setOnClickListener {
            rvCollection.scrollToPosition(0)
            btnBackToTop.visibility = View.GONE
        }

        rvCollection.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun getQuotes(): String {
        var json = "[]"
        try {
            val inputStream = resources.openRawResource(R.raw.quotes)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            return json
        }
        return json
    }

    private fun initialQuote(myCollectionList: MutableList<MyCollection>) {
        PreferenceManager(this).apply {
            saveDate(Date(System.currentTimeMillis()).toString())
            var saved = false
            for (collection in myCollectionList) {
                if (saved)
                    break
                if (getQuotes().isEmpty()) {
                    for ((index, quotes) in collection.quotes.withIndex()) {
                        saveQuotes(collection.quotes[index].quote)
                        saveAuthor(collection.quotes[index].author)
                        saved = true
                        break
                    }
                }
            }
        }
    }

    private fun loadAd() {
        MobileAds.initialize(this) { }
        val adRequest: AdRequest = AdManagerAdRequest.Builder().build()
        if (counter <= 4)
            adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdOpened() {
                counter++
                PreferenceManager(this@MainActivity).apply {
                    saveAdCount(counter)
                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))  //  2 minutes
//                    saveExpiryTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1440))  //  24 hours
                }
                if (counter >= 5)
                    adView.visibility = View.GONE
            }
        }
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