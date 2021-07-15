package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
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
                    saveDate(Date(System.currentTimeMillis()).toString())
                }
            } else
                saveDate(Date(System.currentTimeMillis()).toString())

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
                    etSearch.text.toString()
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

        btnBackToTop.setOnClickListener {
            rvCollection.scrollToPosition(0)
        }

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

}