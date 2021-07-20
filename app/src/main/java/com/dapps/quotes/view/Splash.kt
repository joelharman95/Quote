package com.dapps.quotes.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dapps.quotes.R
import com.dapps.quotes.pref.PreferenceManager
import com.dapps.quotes.utils.GoogleAppCheck.verifyInstallerId
import com.dapps.quotes.utils.showAlert
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        PreferenceManager(this).apply {
            tvQuote.text = getQuotes()
            tvAuthor.text = getAuthor()
        }

        if (verifyInstallerId(this)) {
            showAlert(
                "Seems the app has not downloaded from play store, please install from play store in order to use"
            ) { dialog, which -> finish() }
        } else {
            GlobalScope.launch(Dispatchers.Main) {  //  Working on main thread
                delay(2000)
                startActivity(Intent(this@Splash, MainActivity::class.java))
                this@Splash.finish()
            }
        }
    }

    private fun verifyInstallerIdd(): Boolean {
        val validInstallers = listOf("com.android.vending", "com.google.android.feedback")
        val installer = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)
            packageManager.getInstallSourceInfo(packageName).toString()
        else
            packageManager.getInstallerPackageName(packageName)
        return validInstallers.contains(installer)
    }

}