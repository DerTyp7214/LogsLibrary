/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.logs.R
import com.dertyp7214.logs.helpers.DogbinUtils
import com.dertyp7214.logs.helpers.Logger
import com.dertyp7214.logs.helpers.Ui
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Logs() : Fragment() {

    private var primaryColor: Int = Color.GRAY
    private var accentColor: Int = Color.GRAY

    private val info = "Info"
    private val debug = "Debug"
    private val error = "Error"
    private val crash = "Crash"
    private val assert = "Assert"
    private val warn = "Warn"
    private val verbose = "Verbose"

    private fun stringArrayOf(vararg elements: String): List<String> {
        val list = ArrayList<String>()
        elements.forEach { list.add(it) }
        return list
    }

    init {
        primaryColor = Logger.primaryColor
        accentColor = Logger.accentColor
    }

    constructor(@ColorInt primaryColor: Int, @ColorInt accentColor: Int) : this() {
        this.primaryColor = primaryColor
        this.accentColor = accentColor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.logs, container, false)

        val sharedPreferences = activity!!.getSharedPreferences("logs", Context.MODE_PRIVATE)

        var logs = ArrayList<Pair<String, Pair<String, String>>>()

        sharedPreferences.all.keys.forEach {
            val key = it
            val obj = JSONObject(sharedPreferences.getString(key, ""))
            val type = obj.getString("type")
            val body = obj.getString("body")
            logs.add(Pair(key, Pair(body, type)))
        }

        v.findViewById<ViewGroup>(R.id.layout).setBackgroundColor(primaryColor)

        val rv: RecyclerView = v.findViewById(R.id.rv)
        logs = ArrayList(logs.sortedWith(kotlin.Comparator { o1, o2 ->
            o1.first.toLong().compareTo(o2.first.toLong())
        }).reversed())
        val logList: ArrayList<Pair<String, Pair<String, String>>> =
            logs.clone() as ArrayList<Pair<String, Pair<String, String>>>
        val adapter = LogsAdapter(activity!!, logList)
        rv.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            rv.context,
            layoutManager.orientation
        )
        rv.layoutManager = layoutManager
        rv.addItemDecoration(dividerItemDecoration)

        val list = ArrayList<String>(stringArrayOf(info, debug, error, crash, assert, warn, verbose))
        val dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, list)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinner: Spinner = v.findViewById(R.id.spinner)
        spinner.adapter = dataAdapter
        spinner.setSelection(list.indexOf(verbose))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (list[position]) {
                    info -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.INFO))
                        adapter.notifyDataSetChanged()
                    }
                    debug -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.DEBUG))
                        adapter.notifyDataSetChanged()
                    }
                    error -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.ERROR))
                        adapter.notifyDataSetChanged()
                    }
                    crash -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.CRASH))
                        adapter.notifyDataSetChanged()
                    }
                    assert -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.ASSERT))
                        adapter.notifyDataSetChanged()
                    }
                    warn -> {
                        logList.clear()
                        logList.addAll(filterByType(logs, Logger.Companion.Type.WARN))
                        adapter.notifyDataSetChanged()
                    }
                    verbose -> {
                        logList.clear()
                        logList.addAll(logs)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return v
    }

    private fun filterByType(
        list: ArrayList<Pair<String, Pair<String, String>>>,
        type: Logger.Companion.Type
    ): ArrayList<Pair<String, Pair<String, String>>> {
        return list.filter {
            val t = it.second.second
            t == type.name
        } as ArrayList<Pair<String, Pair<String, String>>>
    }

    private fun stringToType(type: String): Logger.Companion.Type {
        return when (type) {
            Logger.Companion.Type.ERROR.name -> Logger.Companion.Type.ERROR
            Logger.Companion.Type.DEBUG.name -> Logger.Companion.Type.DEBUG
            Logger.Companion.Type.CRASH.name -> Logger.Companion.Type.CRASH
            Logger.Companion.Type.ASSERT.name -> Logger.Companion.Type.ASSERT
            Logger.Companion.Type.WARN.name -> Logger.Companion.Type.WARN
            else -> Logger.Companion.Type.INFO
        }
    }

    private class LogsAdapter(private val activity: Activity, val logs: List<Pair<String, Pair<String, String>>>) :
        RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(activity).inflate(R.layout.log_item, parent, false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int = logs.size

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val pair = logs[position]
            val title = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.UK).format(Date(pair.first.toLong()))

            holder.time.text = title
            holder.body.text = pair.second.first
            holder.type.text = pair.second.second
            if (pair.second.second == Logger.Companion.Type.ERROR.name || pair.second.second == Logger.Companion.Type.CRASH.name)
                holder.type.setTextColor(activity.resources.getColor(android.R.color.holo_red_light))
            else
                holder.type.setTextColor(Ui.getAttrColor(activity, android.R.attr.textColorPrimary))

            holder.layout.setOnClickListener {
                val size = Point()
                activity.windowManager.defaultDisplay.getSize(size)
                val width = size.x
                val builder = AlertDialog.Builder(activity)
                    .setPositiveButton(activity.getString(android.R.string.ok)) { dialog, _ -> dialog.dismiss() }
                    .setTitle(title)
                    .setMessage(pair.second.first)
                if (pair.second.second == Logger.Companion.Type.CRASH.name)
                    builder.setNeutralButton(activity.getString(R.string.share_crash_url)) { dialog, _ ->
                        DogbinUtils.upload(pair.second.first, object : DogbinUtils.UploadResultCallback {
                            override fun onSuccess(url: String) {
                                dialog.dismiss()
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, url)
                                    type = "text/plain"
                                }
                                activity.startActivity(
                                    Intent.createChooser(
                                        sendIntent,
                                        activity.getString(R.string.send_to)
                                    )
                                )
                            }

                            override fun onFail(message: String, e: Exception) {
                                dialog.dismiss()
                                Logger.log(Logger.Companion.Type.ERROR, "Dogbin Upload", Log.getStackTraceString(e))
                            }
                        })
                    }
                val dialog = builder.create()
                dialog.show()
                dialog.window?.decorView?.layoutParams?.width = (width * 0.85F).toInt()
                dialog.window
                    ?.setBackgroundDrawable(ColorDrawable(Ui.getAttrColor(activity, android.R.attr.windowBackground)))
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val time: TextView = v.findViewById(R.id.txt_time)
            val type: TextView = v.findViewById(R.id.txt_type)
            val body: TextView = v.findViewById(R.id.txt_body)
            val layout: ViewGroup = v.findViewById(R.id.layout)
        }
    }
}
