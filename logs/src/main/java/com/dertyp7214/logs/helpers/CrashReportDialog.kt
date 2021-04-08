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
import android.os.Build
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
        val extraData = intent?.extras?.getString("CRASH_EXTRA")
        Logger.log(Logger.Companion.Type.CRASH, log ?: "", log, this)

        title = ""

        val logMessage = StringBuilder("Release: ").apply {
            append(Build.VERSION.RELEASE).append("\n")
            append("SDK: ").append(Build.VERSION.SDK_INT).append("\n")
            append("Codename: ").append(Build.VERSION.CODENAME).append("\n")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                append("Base: ").append(Build.VERSION.BASE_OS).append("\n")
                append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n")
            }
            append("Incremental: ").append(Build.VERSION.INCREMENTAL).append("\n\n")
            append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
            append("Brand: ").append(Build.BRAND).append("\n")
            append("Model: ").append(Build.MODEL).append("\n")
            append("Device: ").append(Build.DEVICE).append("\n")
            append("Bootloader: ").append(Build.BOOTLOADER).append("\n")
            append("Hardware: ").append(Build.HARDWARE).append("\n")
            append("Board: ").append(Build.BOARD).append("\n")
            append("Display: ").append(Build.DISPLAY).append("\n")
            append("Host: ").append(Build.HOST).append("\n")
            append("Id: ").append(Build.ID).append("\n")
            append("Fingerprint: ").append(Build.FINGERPRINT).append("\n\n")
            if (extraData != null) append(extraData)
        }.append("\n\n").append(log).toString()

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.app_crash)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finishAndRemoveTask()
            }
            .setNeutralButton(getString(R.string.share_crash_url)) { dialog, _ ->
                DogbinUtils.upload(logMessage, object : DogbinUtils.UploadResultCallback {
                    override fun onSuccess(url: String) {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)))
                    }

                    override fun onFail(message: String, e: Exception) {
                        dialog.dismiss()
                        Logger.log(
                            Logger.Companion.Type.ERROR,
                            "Dogbin Upload",
                            Log.getStackTraceString(e)
                        )
                        finishAndRemoveTask()
                    }
                })
            }
            .create()
        dialog.show()
        dialog.getButton(BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)
        dialog.getButton(BUTTON_NEUTRAL).setBackgroundColor(Color.TRANSPARENT)
        dialog.getButton(BUTTON_POSITIVE).setTextColor(Logger.accentColor)
        dialog.getButton(BUTTON_NEUTRAL).setTextColor(Logger.accentColor)
    }
}
