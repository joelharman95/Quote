package com.dapps.quotes.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dapps.quotes.R
import kotlinx.android.synthetic.main.layout_category_item.view.*

typealias myCollection = (MyCollection) -> Unit

data class MyCollection(
    val category: String = "",
    val count: String? = null,
)

class CollectionAdapter(private val myCollection: myCollection) : RecyclerView.Adapter<CollectionAdapter.CollectionHolder>() {

    private val collectionList = mutableListOf<MyCollection>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        return CollectionHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int) {
        holder.bindUI(position)
    }

    override fun getItemCount() = collectionList.size

    fun setCollectionList(_collectionList: List<MyCollection>) {
        collectionList.clear()
        collectionList.addAll(_collectionList)
        notifyDataSetChanged()
    }

    inner class CollectionHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindUI(position: Int) {
            view.apply {
                collectionList[position].let { collection ->
                    tvCategory.text = collection.category
                    tvCount.text = "${collection.count}"
                    setOnClickListener {
                        myCollection.invoke(collection)
                    }
                }
            }
        }
    }

}