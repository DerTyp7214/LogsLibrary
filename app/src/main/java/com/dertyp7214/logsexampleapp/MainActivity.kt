package com.dertyp7214.logsexampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dertyp7214.logs.helpers.Logger
import com.dertyp7214.logs.screens.Logs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, Preferences())
            commit()
        }

        Logger.getLogs(this).forEach { (t, u) ->
            Log.d("LOOOOGGGS", "$t: ${u.get("body")}")
        }

        Log.d("LOOOOOGS", Logger.logsToMessage(this))

        findViewById<Toolbar>(R.id.toolbar2).apply {
            menu.add(0, R.id.menu_open, 0, "Open").setOnMenuItemClickListener {
                startActivity(Intent(this@MainActivity, Logs::class.java))
                true
            }
            menu.add(0, R.id.menu_crash, 0, "Crash").setOnMenuItemClickListener {
                val i = 55 / 0
                true
            }
            menu.add(0, R.id.menu_clear, 0, "Clear").setOnMenuItemClickListener {
                Logger.clear()
                recreate()
                true
            }
        }
    }
}
