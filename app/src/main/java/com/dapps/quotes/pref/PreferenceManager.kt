package com.dapps.quotes.pref

import android.content.Context
import androidx.core.content.edit
import com.dapps.quotes.BuildConfig

class PreferenceManager(context: Context) : IPreferenceManager {

    private val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun saveDate(date: String) {
        pref.edit {
            putString(DATEE, date)
        }
    }

    override fun getDate() = pref.getString(DATEE, "").toString()

    companion object {
        const val PREFERENCE_NAME = BuildConfig.APPLICATION_ID;
        const val DATEE = "datee"
    }
}