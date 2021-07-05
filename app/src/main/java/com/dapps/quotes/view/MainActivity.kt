package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants.COLLECTION
import com.dapps.quotes.utils.Constants.COLLECTION_COUNT
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myCollectionList = listOf<MyCollection>(
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
        rvCollection.adapter = CollectionAdapter(myCollectionList) { myCollection ->
            val intent = Intent(this, QuotesActivity::class.java)
            intent.putExtra(COLLECTION, myCollection.category)
            intent.putExtra(COLLECTION_COUNT, myCollection.count)
            startActivity(intent)
        }
        rvCollection.requestFocus()

        btnBackToTop.setOnClickListener {
            rvCollection.scrollToPosition(0)
        }

    }
}