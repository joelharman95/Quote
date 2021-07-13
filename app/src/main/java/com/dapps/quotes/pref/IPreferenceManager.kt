package com.dapps.quotes.pref

interface IPreferenceManager {

    fun saveDate(date: String)
    fun getDate(): String
    fun saveQuotes(quote: String)
    fun getQuotes(): String
    fun saveAuthor(author: String)
    fun getAuthor(): String

}