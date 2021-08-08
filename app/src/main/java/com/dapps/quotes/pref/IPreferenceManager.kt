package com.dapps.quotes.pref

interface IPreferenceManager {

    fun saveDate(date: String)
    fun getDate(): String
    fun saveQuotes(quote: String)
    fun getQuotes(): String
    fun saveAuthor(author: String)
    fun getAuthor(): String
    fun saveAdCount(count: Int)
    fun getAdCount(): Int
    fun saveExpiryTime(time: Long)
    fun getExpiryTime(): Long

}