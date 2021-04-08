package com.dertyp7214.logs.helpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.R
import org.json.JSONArray
import org.json.JSONObject
import kotlin.system.exitProcess

class Logger {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var primaryColor = Color.GRAY
        var accentColor = Color.GRAY

        var extraData: (() -> String)? = null

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

        fun getAll(): Map<Type, JSONObject> {
            return PreferenceManager.getDefaultSharedPreferences(context).all.map {
                val json = JSONObject(it.value as String)
                val type = when (json.getString("type")) {
                    Type.CRASH.name -> Type.CRASH
                    Type.DEBUG.name -> Type.DEBUG
                    Type.ERROR.name -> Type.ERROR
                    Type.INFO.name -> Type.INFO
                    Type.ASSERT.name -> Type.ASSERT
                    Type.WARN.name -> Type.WARN
                    else -> Type.VERBOSE
                }
                Pair(type, json)
            }.toMap()
        }

        fun log(type: Type, tag: String, body: Any?, c: Context = context) {
            val mode =
                parseLogMode(
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("logMode", "VERBOSE")!!
                )
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
                val sharedPreferences = c.getSharedPreferences("logs", MODE_PRIVATE)
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
                            JSONObject().apply {
                                put("type", type.name)
                                put("body", body.toString())
                            }.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getLogs(c: Context = context): Map<Long, JSONObject> {
            val map = HashMap<Long, JSONObject>()
            val sharedPreferences = c.getSharedPreferences("logs", MODE_PRIVATE)
            sharedPreferences.all.forEach { (k, v) ->
                map[k.toLong()] = try {
                    JSONObject(v.toString())
                } catch (e: java.lang.Exception) {
                    JSONObject()
                }
            }
            return map.toSortedMap { o1, o2 -> (o1 - o2).toInt() }
        }

        fun logsToMessage(c: Context = context): String {
            return JSONArray().apply {
                getLogs(c).forEach {
                    put(it.value.apply { put("time", it.key) })
                }
            }.toString(2)
        }

        fun clear() {
            context.getSharedPreferences("logs", MODE_PRIVATE).edit {
                clear()
            }
        }

        fun init(application: Application) {
            context = application.applicationContext
            setUpCrashHelper(application.applicationContext)
            this.primaryColor = context.resources.getColor(R.color.colorPrimary)
            this.accentColor = context.resources.getColor(R.color.colorAccent)
        }

        fun init(
            application: Application,
            @ColorInt primaryColor: Int,
            @ColorInt accentColor: Int
        ) {
            init(application)
            this.primaryColor = primaryColor
            this.accentColor = accentColor
        }

        private fun setUpCrashHelper(applicationContext: Context) {
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                val dialogIntent = Intent(applicationContext, CrashReportDialog::class.java)
                dialogIntent.putExtra("CRASH_LOG", Log.getStackTraceString(e ?: Error()))
                dialogIntent.putExtra("CRASH_MESSAGE", e.message)
                dialogIntent.putExtra("CRASH_EXTRA", extraData?.invoke())
                dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(dialogIntent)
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(10)
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