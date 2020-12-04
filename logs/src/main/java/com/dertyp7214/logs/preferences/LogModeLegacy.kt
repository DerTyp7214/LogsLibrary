package com.dertyp7214.logs.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger

class LogModeLegacy : DropDownPreference {
    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(attrs)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(Logger.context)
        setDefaultValue("VERBOSE")
        entries = arrayOf("VERBOSE", "ASSERT", "INFO", "WARN", "DEBUG", "ERROR", "CRASH")
        entryValues = arrayOf("VERBOSE", "ASSERT", "INFO", "WARN", "DEBUG", "ERROR", "CRASH")
        key = "logMode"
        title = "Log-mode (${preferences.getString(key, "VERBOSE")})"
        setOnPreferenceChangeListener { _, newValue ->
            title = "Log-mode ($newValue)"
            true
        }
    }
}
