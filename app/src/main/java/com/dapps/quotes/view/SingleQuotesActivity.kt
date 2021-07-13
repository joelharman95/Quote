package com.dapps.quotes.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.utils.Constants
import com.dapps.quotes.utils.Constants.POSITION
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
        setQuotes(myQuotesList[intent.getIntExtra(POSITION, 0)], intent.getIntExtra(POSITION, 0))

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
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData =
                ClipData.newPlainText("quote", "${tvQuote.text}\n${tvAuthor.text}")
            clipboardManager.setPrimaryClip(clipData)
//            btnCopy.background = AppCompatResources.getDrawable(baseContext, R.drawable.bg_copied)
            btnCopy.setBackgroundColor(getColor(R.color.card_bg))
            btnCopy.strokeColor = ColorStateList.valueOf(getColor(R.color.card_bg))
            btnCopy.text = "Copied!!!"
        }

    }

    private fun setQuotes(myQuote: MyQuote, position: Int) {
//        btnCopy.background = AppCompatResources.getDrawable(baseContext, R.drawable.search_view_border)
        btnCopy.setBackgroundColor(getColor(R.color.white))
        btnCopy.strokeColor = ColorStateList.valueOf(getColor(R.color.black))
        btnCopy.text = getString(R.string.action_copy_quotes)
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