package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.dapps.quotes.utils.Constants.COLLECTION
import com.dapps.quotes.utils.getDateFrom
import com.dapps.quotes.utils.hideKeyboard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {
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
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
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
            val inputStream = getResources().openRawResource(R.raw.quotes);
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

}