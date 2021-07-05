package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btnBackToTop
import kotlinx.android.synthetic.main.activity_quotes.*
import kotlinx.android.synthetic.main.activity_quotes.tvCount
import kotlinx.android.synthetic.main.activity_quotes.tvQuote

class QuotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        val quotesCount = intent.getIntExtra(Constants.COLLECTION_COUNT, 0)
        tvQuote.text = intent.getStringExtra(Constants.COLLECTION)
        tvCount.text = "${quotesCount} Quotes"

        val myQuotesList = listOf(
            MyQuote("“Be yourself; everyone else is already taken.”", "― Oscar Wilde"),
            MyQuote("“Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.”", "― Albert Einstein"),
            MyQuote("“So many books, so little time.”", "― Frank Zappa"),
            MyQuote("“A room without books is like a body without a soul.”",  "― Marcus Tullius Cicero"),
            MyQuote("“You know you're in love when you can't fall asleep because reality is finally better than your dreams.”", "― Dr. Seuss"),
            MyQuote("“So many books, so little time.”", "― Frank Zappa"),
            MyQuote("“A room without books is like a body without a soul.”", "― Marcus Tullius Cicero"),
        )
        rvQuotes.adapter = QuotesAdapter(myQuotesList) { myQuote ->
            val intent = Intent(this, SingleQuotesActivity::class.java)
            intent.putExtra(Constants.QUOTES, Gson().toJson(myQuotesList))
            intent.putExtra(Constants.COLLECTION_COUNT, quotesCount)
            startActivity(intent)
        }
        rvQuotes.requestFocus()

        btnBackToTop.setOnClickListener {
            rvQuotes.scrollToPosition(0)
        }

    }
}