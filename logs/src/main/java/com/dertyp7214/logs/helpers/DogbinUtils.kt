/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.helpers

import android.os.Handler
import android.os.HandlerThread
import android.util.JsonReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object DogbinUtils {
    private const val BASE_URL = "https://del.dog"
    private const val API_URL = "$BASE_URL/documents"
    private var handler: Handler? = null

    fun upload(content: String, callback: UploadResultCallback) {
        getHandler().post {
            try {
                val urlConnection = URL(API_URL).openConnection() as HttpsURLConnection
                try {
                    urlConnection.setRequestProperty("Accept-Charset", "UTF-8")
                    urlConnection.doOutput = true

                    urlConnection.outputStream.use { output -> output.write(content.toByteArray(charset("UTF-8"))) }
                    var key = ""
                    JsonReader(
                        InputStreamReader(urlConnection.inputStream, "UTF-8")
                    ).use { reader ->
                        reader.beginObject()
                        while (reader.hasNext()) {
                            val name = reader.nextName()
                            if (name == "key") {
                                key = reader.nextString()
                                break
                            } else {
                                reader.skipValue()
                            }
                        }
                        reader.endObject()
                    }
                    if (key.isNotEmpty()) {
                        callback.onSuccess(getUrl(key))
                    } else {
                        val msg = "Failed to upload to dogbin: No key retrieved"
                        callback.onFail(msg, DogbinException(msg))
                    }
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                callback.onFail("Failed to upload to dogbin", e)
            }
        }
    }

    private fun getUrl(key: String): String {
        return "$BASE_URL/$key"
    }

    private fun getHandler(): Handler {
        if (handler == null) {
            val handlerThread = HandlerThread("dogbinThread")
            if (!handlerThread.isAlive)
                handlerThread.start()
            handler = Handler(handlerThread.looper)
        }
        return handler!!
    }

    interface UploadResultCallback {
        fun onSuccess(url: String)

        fun onFail(message: String, e: Exception)
    }

    private class DogbinException(message: String) : Exception(message) {
        companion object {
            private const val serialVersionUID = 666L
        }
    }
}