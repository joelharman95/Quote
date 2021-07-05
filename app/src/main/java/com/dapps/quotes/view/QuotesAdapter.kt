package com.dapps.quotes.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dapps.quotes.R
import kotlinx.android.synthetic.main.layout_category_item.view.*

typealias myQuote = (MyQuote) -> Unit

data class MyQuote(
    val category: String? = null,
    val author: String? = null,
)

class QuotesAdapter(val collectionList: List<MyQuote>, val myQuote: myQuote) :
    RecyclerView.Adapter<QuotesAdapter.QuotesHolder>() {

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

    inner class QuotesHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindUI(position: Int) {
            view.apply {
                collectionList[position].let { collection ->
                    tvCategory.text = collection.category
                    tvCount.text = "${collection.author}"
                    setOnClickListener {
                        myQuote.invoke(collection)
                    }
                }
            }
        }
    }

}