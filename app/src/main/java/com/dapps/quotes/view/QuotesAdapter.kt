package com.dapps.quotes.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dapps.quotes.R
import kotlinx.android.synthetic.main.layout_category_item.view.*

typealias myQuote = (Quotes, Int) -> Unit

class QuotesAdapter(private val myQuote: myQuote) :
    RecyclerView.Adapter<QuotesAdapter.QuotesHolder>() {
    private val collectionList = mutableListOf<Quotes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotesHolder {
        return QuotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuotesHolder, position: Int) {
        holder.bindUI(position)
    }

    override fun getItemCount() = collectionList.size

    fun setQuotesList(_collectionList: List<Quotes>) {
        collectionList.clear()
        collectionList.addAll(_collectionList)
        notifyDataSetChanged()
    }

    inner class QuotesHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindUI(position: Int) {
            view.apply {
                collectionList[position].let { collection ->
                    tvCount.setTextColor(ContextCompat.getColor(this.context, R.color.black))
                    tvCategory.text = collection.quote
                    tvCount.text = "${collection.author}"
                    setOnClickListener {
                        myQuote.invoke(collection, position)
                    }
                }
            }
        }
    }

}