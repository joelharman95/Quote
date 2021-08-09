package com.dapps.quotes.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dapps.quotes.BuildConfig
import com.dapps.quotes.R
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

fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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

fun Activity.showAlert(msg: String, dialogInterface: DialogInterface.OnClickListener) {
    val pickDialog = android.app.AlertDialog.Builder(this)
    pickDialog.setCancelable(false)
    pickDialog.setPositiveButton(
        resources.getString(R.string.action_exit), dialogInterface
    )
    pickDialog.setMessage(msg)
    pickDialog.show()
}

fun Activity.shareApp() {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(
        Intent.EXTRA_TEXT,
        "Hello, Check Out our Daily Quotes from Eagle App Buffer. Download it from https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
    )
    intent.type = "text/plain"
    val shareIntent = Intent.createChooser(intent, null)
    startActivity(shareIntent)
}

fun Activity.openBrowser() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
    startActivity(Intent.createChooser(intent, "Choose Your browser"))
}

fun Activity.openMail() {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.type = "text/plain"
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("eagleappbuffer@gmail.com"))
//    intent.putExtra(Intent.EXTRA_SUBJECT, "Mail Subject")
//    intent.putExtra(Intent.EXTRA_TEXT, "massage")
    intent.setPackage("com.google.android.gm")
    try {
        startActivity(Intent.createChooser(intent, "Send mail..."))
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
    }
}