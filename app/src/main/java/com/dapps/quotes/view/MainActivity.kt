package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants.COLLECTION
import com.dapps.quotes.utils.Constants.COLLECTION_COUNT
import com.dapps.quotes.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myCollectionList = mutableListOf<MyCollection>()
        try {
            val jsonArray = JSONArray(getQuotes())
            for (a in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(a)
                val myCollection = MyCollection(jsonObject.getString("title"), jsonObject.getString("count"))
                myCollectionList.add(myCollection)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
        }
        ivSearch.setOnClickListener {
            (rvCollection.adapter as CollectionAdapter).setCollectionList(myCollectionList.filter { collection ->
                collection.category.toLowerCase(Locale.getDefault()).contains(
                    etSearch.text.toString()
                )
            })
            it.hideKeyboard()
        }
        rvCollection.adapter =
            CollectionAdapter { myCollection ->
                val intent = Intent(this, QuotesActivity::class.java)
                intent.putExtra(COLLECTION, myCollection.category)
                intent.putExtra(COLLECTION_COUNT, myCollection.count)
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