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
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myCollectionList = listOf(
            MyCollection("Motivation", 512),
            MyCollection("Love", 152),
            MyCollection("Positivity", 512),
            MyCollection("Sayings", 50),
            MyCollection("Family", 250),
            MyCollection("Life", 45),
            MyCollection("Good Morning", 259),
            MyCollection("Good Evening", 257),
            MyCollection("Good Night", 258),
            MyCollection("Billionairs", 28),
            MyCollection("New Year", 280),
            MyCollection("Funny", 487),
        )

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

        fabToTop.setOnClickListener {
            rvCollection.scrollToPosition(0)
        }

    }
}