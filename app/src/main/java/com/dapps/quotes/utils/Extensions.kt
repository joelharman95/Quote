package com.dapps.quotes.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

fun View.showKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun String.getDateFrom(from: String, to: String): Long {
    return try {
        val sdf = SimpleDateFormat(from, Locale.getDefault())
        val utc = TimeZone.getTimeZone("UTC")
        val destFormat = SimpleDateFormat(to, Locale.getDefault())
        sdf.timeZone = utc
        val convertedDate = sdf.parse(this)
        val newDate = destFormat.format(convertedDate)
        newDate.toString().convertDateToLong(to)
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun String.convertDateToLong(to: String): Long {
    val df = SimpleDateFormat(to, Locale.getDefault())
    return df.parse(this).time
}
