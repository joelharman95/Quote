/*
 * *
 *  * Created by Nethaji on 28/6/20 10:03 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/6/20 10:03 AM
 *
 */

package com.dapps.quotes.utils

import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans

fun View.getCompatColor(@ColorRes colorRes: Int): Int =
    ContextCompat.getColor(this.context, colorRes)

fun TextView.applySpanPo(stringPrefix: String, color: Int, stringSuffix: String) {
    text = buildSpannedString {
        inSpans(
            ForegroundColorSpan(getCompatColor(color))
        ) {
            bold { append(stringPrefix) }
        }.inSpans(RelativeSizeSpan(0.6f)) {
            append(stringSuffix)
        }
    }
}