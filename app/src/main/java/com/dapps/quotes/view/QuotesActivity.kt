package com.dapps.quotes.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val myCollection =
            Gson().fromJson(intent.getStringExtra(Constants.COLLECTION), MyCollection::class.java)
        val myQuotesList =
            if (myCollection.quotes.isNullOrEmpty()) listOf() else myCollection.quotes
        tvQuote.text = myCollection.title
        tvCount.text = "${myCollection.count} Quotes"

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                ivSearch.performClick()
            false
        }
        val filteredList = mutableListOf<Quotes>()
        filteredList.addAll(myQuotesList)
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

}