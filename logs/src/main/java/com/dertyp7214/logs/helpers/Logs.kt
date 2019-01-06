/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.helpers

import android.app.Application
import androidx.annotation.ColorInt

@Deprecated("Use 'Logger' instead", ReplaceWith("Logs", "Logger"), DeprecationLevel.WARNING)
class Logs {
    companion object {
        fun log(type: Type, tag: String, body: Any?) {
            Logger.log(Type.getByName(type), tag, body)
        }

        fun init(application: Application) {
            init(application)
        }

        fun init(application: Application, @ColorInt primaryColor: Int, @ColorInt accentColor: Int) {
            init(application, primaryColor, accentColor)
        }

        enum class Type {
            DEBUG,
            ERROR,
            WARN,
            ASSERT,
            INFO,
            CRASH;
            companion object {
                fun getByName(type: Type): Logger.Companion.Type {
                    return when (type) {
                        DEBUG -> Logger.Companion.Type.DEBUG
                        ERROR -> Logger.Companion.Type.ERROR
                        WARN -> Logger.Companion.Type.WARN
                        ASSERT -> Logger.Companion.Type.ASSERT
                        INFO -> Logger.Companion.Type.INFO
                        CRASH -> Logger.Companion.Type.CRASH
                    }
                }
            }
        }
    }
}