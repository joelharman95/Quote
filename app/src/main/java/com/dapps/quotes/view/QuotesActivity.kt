package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants
import com.dapps.quotes.utils.hideKeyboard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btnBackToTop
import kotlinx.android.synthetic.main.activity_quotes.*
import kotlinx.android.synthetic.main.activity_quotes.etSearch
import kotlinx.android.synthetic.main.activity_quotes.ivSearch
import kotlinx.android.synthetic.main.activity_quotes.tvCount
import kotlinx.android.synthetic.main.activity_quotes.tvQuote
import java.util.*

class QuotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        val myCollection = Gson().fromJson(intent.getStringExtra(Constants.COLLECTION), MyCollection::class.java)
        val myQuotesList = if (myCollection.quotes.isNullOrEmpty()) listOf() else myCollection.quotes
        tvQuote.text = myCollection.title
        tvCount.text = "${myCollection.count} Quotes"

        /*val myQuotesList = listOf(
            MyQuote("“Be yourself; everyone else is already taken.”", "― Oscar Wilde"),
            MyQuote(
                "“Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.”",
                "― Albert Einstein"
            ),
            MyQuote("“So many books, so little time.”", "― Frank Zappa"),
            MyQuote(
                "“A room without books is like a body without a soul.”",
                "― Marcus Tullius Cicero"
            ),
            MyQuote(
                "“You know you're in love when you can't fall asleep because reality is finally better than your dreams.”",
                "― Dr. Seuss"
            ),
            MyQuote("“So many books, so little time.”", "― Frank Zappa"),
            MyQuote(
                "“A room without books is like a body without a soul.”",
                "― Marcus Tullius Cicero"
            ),
        )*/

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
        }
        ivSearch.setOnClickListener {
            (rvQuotes.adapter as QuotesAdapter).setQuotesList(myQuotesList.filter { quote ->
                (quote.quote + " " + quote.author).toLowerCase(Locale.getDefault()).contains(
                    etSearch.text.toString()
                )
            })
            it.hideKeyboard()
        }
        rvQuotes.adapter = QuotesAdapter { myQuote, position ->
            val intent = Intent(this, SingleQuotesActivity::class.java)
            val filteredList = myQuotesList.filter { quote ->
                (quote.quote + " " + quote.author).toLowerCase(Locale.getDefault()).contains(
                    etSearch.text.toString()
                )
            }
            intent.putExtra(Constants.QUOTES, Gson().toJson(filteredList))
            intent.putExtra(Constants.COLLECTION_COUNT, filteredList.size)
            intent.putExtra(Constants.POSITION, position)
            startActivity(intent)
        }
        rvQuotes.requestFocus()
        (rvQuotes.adapter as QuotesAdapter).setQuotesList(myQuotesList)

        btnBackToTop.setOnClickListener {
            rvQuotes.scrollToPosition(0)
        }

    }
}