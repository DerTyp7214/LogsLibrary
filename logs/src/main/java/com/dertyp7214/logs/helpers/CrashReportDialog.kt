/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.helpers

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dertyp7214.logs.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.app_crash)
            .setMessage(message)
            .setCancelable(false)
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<View>(android.R.id.content)?.background =
            ContextCompat.getDrawable(this, R.drawable.dialog_background)
        dialog.show()
    }
}
