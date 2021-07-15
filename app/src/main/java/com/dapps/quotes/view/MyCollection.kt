package com.dapps.quotes.view

/*data class MyCollection(
    val myCollection: List<MyCollectionItem>
)*/

data class Quotes(
    val quote: String,
    val author: String
)

data class MyCollection(
    val count: String,
    val title: String,
    val quotes: List<Quotes>
)
