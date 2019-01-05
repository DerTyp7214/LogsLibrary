/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.helpers

import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.R

class CrashReportDialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_report_dialog)

        val log = intent?.extras?.getString("CRASH_LOG")
        val message = intent?.extras?.getString("CRASH_MESSAGE")
        Log.d("LOHL", "CRASH")

        title = ""

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.app_crash)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNeutralButton(getString(R.string.share_crash_url)) { dialog, _ ->
                DogbinUtils.upload(log ?: "", object : DogbinUtils.UploadResultCallback {
                    override fun onSuccess(url: String) {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)))
                        dialog.dismiss()
                        finish()
                    }

                    override fun onFail(message: String, e: Exception) {
                        dialog.dismiss()
                        Logs.log(Logs.Companion.Type.ERROR, "Dogbin Upload", Log.getStackTraceString(e))
                        finish()
                    }
                })
            }
            .create()
        dialog.show()
        dialog.getButton(BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)
        dialog.getButton(BUTTON_NEUTRAL).setBackgroundColor(Color.TRANSPARENT)
        dialog.getButton(BUTTON_POSITIVE).setTextColor(Logs.accentColor)
        dialog.getButton(BUTTON_NEUTRAL).setTextColor(Logs.accentColor)
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun getNavBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}
