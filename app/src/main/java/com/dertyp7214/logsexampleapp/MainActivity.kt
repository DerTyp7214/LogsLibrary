package com.dertyp7214.logsexampleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.screens.Logs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        com.dertyp7214.logs.helpers.Logs.log(com.dertyp7214.logs.helpers.Logs.Companion.Type.DEBUG, "", "Debug")
        com.dertyp7214.logs.helpers.Logs.log(com.dertyp7214.logs.helpers.Logs.Companion.Type.WARN, "", "Warn")
        com.dertyp7214.logs.helpers.Logs.log(com.dertyp7214.logs.helpers.Logs.Companion.Type.ASSERT, "", "Assert")
        com.dertyp7214.logs.helpers.Logs.log(com.dertyp7214.logs.helpers.Logs.Companion.Type.CRASH, "", "Crash")
        com.dertyp7214.logs.helpers.Logs.log(com.dertyp7214.logs.helpers.Logs.Companion.Type.INFO, "", "Info")

        startActivity(Intent(this, Logs::class.java))
    }
}
