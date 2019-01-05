package com.dertyp7214.logsexampleapp

import android.app.Application
import com.dertyp7214.logs.helpers.Logs

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Logs.init(this)
    }
}