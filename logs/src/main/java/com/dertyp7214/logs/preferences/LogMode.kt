package com.dertyp7214.logs.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import com.dertyp7214.logs.helpers.Logger

class LogMode : BottomSheetPreference {
    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init()
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    override fun init() {
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
