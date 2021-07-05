package com.dapps.quotes.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants
import com.dapps.quotes.utils.applySpanPo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quotes.tvCount
import kotlinx.android.synthetic.main.activity_quotes.tvQuote
import kotlinx.android.synthetic.main.activity_single_quotes.*

class SingleQuotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_quotes)

        val type = object : TypeToken<List<MyQuote>>() {}.type
        val myQuotesList: MutableList<MyQuote> =
            Gson().fromJson(intent.getStringExtra(Constants.QUOTES), type)
        setQuotes(myQuotesList[0], 0)

        ivLeft.setOnClickListener {
            if ((tvCount.tag as Int) == 0) {
                setQuotes(myQuotesList[myQuotesList.size - 1], myQuotesList.size - 1)
            } else if ((tvCount.tag as Int) <= myQuotesList.size - 1) {
                setQuotes(myQuotesList[(tvCount.tag as Int) - 1], (tvCount.tag as Int) - 1)
            }
        }
        ivRight.setOnClickListener {
            if ((tvCount.tag as Int) >= 0 && (tvCount.tag as Int) < myQuotesList.size - 1) {
                setQuotes(myQuotesList[(tvCount.tag as Int) + 1], (tvCount.tag as Int) + 1)
            } else if ((tvCount.tag as Int) == myQuotesList.size - 1) {
                setQuotes(myQuotesList[0], 0)
            }
        }

        btnCopy.setOnClickListener {
            btnCopy.background = AppCompatResources.getDrawable(baseContext, R.drawable.bg_copied)
            btnCopy.text = "Copied!!!"
        }

    }

    private fun setQuotes(myQuote: MyQuote, position: Int) {
        tvCount.applySpanPo(
            "${position + 1}",
            R.color.black,
            " Of ${intent.getIntExtra(Constants.COLLECTION_COUNT, 0)}"
        )
        tvCount.tag = position
        tvQuote.text = myQuote.category
        tvAuthor.text = myQuote.author
    }

}