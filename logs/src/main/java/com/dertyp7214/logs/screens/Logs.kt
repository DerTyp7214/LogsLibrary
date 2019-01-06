package com.dertyp7214.logs.screens

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.dertyp7214.logs.R
import com.dertyp7214.logs.fragments.Logs
import com.dertyp7214.logs.helpers.Ui
import com.dertyp7214.logs.helpers.Ui.Companion.isColorDark
import kotlinx.android.synthetic.main.activity_logs.*

class Logs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_logs)
        setSupportActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val windowBackground = Ui.getAttrColor(this, android.R.attr.windowBackground)
            toolbar.elevation = 0F
            window.statusBarColor = com.dertyp7214.logs.helpers.Logger.primaryColor
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.navigationBarColor = windowBackground
                if (!isColorDark(windowBackground) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    window.navigationBarDividerColor = Color.LTGRAY
            }
        }

        val itemDelete =
            toolbar.menu.add(0, R.id.menu_delete, Menu.NONE, R.string.menu_action_delete)
        itemDelete.setIcon(R.drawable.ic_action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        itemDelete.setOnMenuItemClickListener {
            getSharedPreferences("logs", Context.MODE_PRIVATE).edit {
                clear()
            }
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame, Logs())
                commit()
            }
            true
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, Logs())
            commit()
        }
    }
}
