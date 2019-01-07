package com.dertyp7214.logsexampleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.helpers.Logger
import com.dertyp7214.logs.screens.Logs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, com.dertyp7214.logs.fragments.Logs())
            commit()
        }

        toolbar2.menu.add(0, R.id.menu_open, 0, "Open").setOnMenuItemClickListener {
            startActivity(Intent(this, Logs::class.java))
            true
        }
        toolbar2.menu.add(0, R.id.menu_crash, 0, "Crash").setOnMenuItemClickListener {
            val i = 55/0
            true
        }
        toolbar2.menu.add(0, R.id.menu_clear, 0, "Clear").setOnMenuItemClickListener {
            Logger.clear()
            recreate()
            true
        }
    }
}
