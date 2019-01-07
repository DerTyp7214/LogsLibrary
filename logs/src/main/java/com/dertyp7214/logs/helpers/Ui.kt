/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.helpers

import android.content.Context
import android.graphics.Color

class Ui {
    companion object {
        fun getAttrColor(context: Context, attr: Int): Int {
            return try {
                val ta = context.obtainStyledAttributes(intArrayOf(attr))
                val colorAccent = ta.getColor(0, 0)
                ta.recycle()
                colorAccent
            } catch (e: Exception) {
                Logger.log(Logger.Companion.Type.ERROR, "getAttrColor", e.message)
                Color.RED
            }
        }

        fun isColorDark(color: Int): Boolean {
            val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            return darkness >= 0.5
        }
    }
}