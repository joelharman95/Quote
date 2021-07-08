package com.dapps.quotes.pref

interface IPreferenceManager {

    fun saveDate(date: String)
    fun getDate(): String

}