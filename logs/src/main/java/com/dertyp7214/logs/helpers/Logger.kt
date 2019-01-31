package com.dertyp7214.logs.helpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.edit
import com.dertyp7214.logs.R
import org.json.JSONObject

class Logger {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var primaryColor = Color.GRAY
        var accentColor = Color.GRAY

        private fun parseLogMode(mode: String): Int {
            return when (mode) {
                "CRASH" -> 1
                "ERROR" -> 2
                "DEBUG" -> 3
                "WARN" -> 4
                "INFO" -> 5
                "ASSERT" -> 6
                else -> 7
            }
        }

        fun log(type: Type, tag: String, body: Any?, c: Context = context) {
            val mode =
                parseLogMode(PreferenceManager.getDefaultSharedPreferences(context).getString("logMode", "VERBOSE")!!)
            when {
                type == Type.CRASH && mode >= 1 -> Log.wtf(tag, body.toString())
                type == Type.ERROR && mode >= 2 -> Log.e(tag, body.toString())
                type == Type.DEBUG && mode >= 3 -> Log.d(tag, body.toString())
                type == Type.WARN && mode >= 4 -> Log.w(tag, body.toString())
                type == Type.INFO && mode >= 5 -> Log.i(tag, body.toString())
                type == Type.ASSERT && mode >= 6 -> Log.i(tag, body.toString())
                type == Type.VERBOSE && mode >= 7 -> Log.v(tag, body.toString())
            }
            try {
                val sharedPreferences = c.getSharedPreferences("logs", Context.MODE_PRIVATE)
                if ((type == Type.CRASH && mode >= 1)
                    || (type == Type.ERROR && mode >= 2)
                    || (type == Type.DEBUG && mode >= 3)
                    || (type == Type.WARN && mode >= 4)
                    || (type == Type.INFO && mode >= 5)
                    || (type == Type.ASSERT && mode >= 6)
                    || (type == Type.VERBOSE && mode >= 7)
                ) {
                    sharedPreferences.edit {
                        putString(
                            System.currentTimeMillis().toString(),
                            JSONObject("{\"type\": \"${type.name}\", \"body\": \"${body.toString()}\"}").toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun clear() {
            context.getSharedPreferences("logs", Context.MODE_PRIVATE).edit {
                clear()
            }
        }

        fun init(application: Application) {
            context = application.applicationContext
            setUpCrashHelper(application.applicationContext)
            this.primaryColor = context.resources.getColor(R.color.colorPrimary)
            this.accentColor = context.resources.getColor(R.color.colorAccent)
        }

        fun init(application: Application, @ColorInt primaryColor: Int, @ColorInt accentColor: Int) {
            init(application)
            this.primaryColor = primaryColor
            this.accentColor = accentColor
        }

        private fun setUpCrashHelper(applicationContext: Context) {
            Thread.setDefaultUncaughtExceptionHandler { t, e ->
                val dialogIntent = Intent(applicationContext, CrashReportDialog::class.java)
                dialogIntent.putExtra("CRASH_LOG", Log.getStackTraceString(e ?: Error()))
                dialogIntent.putExtra("CRASH_MESSAGE", e?.message)
                dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(dialogIntent)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(10)
            }
        }

        enum class Type {
            DEBUG,
            ERROR,
            WARN,
            ASSERT,
            INFO,
            CRASH,
            VERBOSE
        }
    }
}