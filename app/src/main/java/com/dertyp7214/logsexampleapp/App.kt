package com.dertyp7214.logsexampleapp

import android.app.Application
import com.dertyp7214.logs.helpers.Logger

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.init(this)
    }
}