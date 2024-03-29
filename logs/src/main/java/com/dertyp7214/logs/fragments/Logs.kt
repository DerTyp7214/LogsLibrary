/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.logs.R
import com.dertyp7214.logs.helpers.DogbinUtils
import com.dertyp7214.logs.helpers.Logger
import com.dertyp7214.logs.helpers.Ui
import com.dertyp7214.preferencesplus.core.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class Logs : Fragment() {

    private val info = "Info"
    private val debug = "Debug"
    private val error = "Error"
    private val crash = "Crash"
    private val assert = "Assert"
    private val warn = "Warn"
    private val verbose = "Verbose"

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.logs, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("logs", Context.MODE_PRIVATE)

        var logs = ArrayList<Pair<String, Pair<String, String>>>()

        sharedPreferences.all.keys.forEach {
            val key = it
            val obj = JSONObject(sharedPreferences.getString(key, "")!!)
            val type = obj.getString("type")
            val body = obj.getString("body")
            logs.add(Pair(key, Pair(body, type)))
        }

        val rv: RecyclerView = v.findViewById(R.id.rv)
        logs = ArrayList(logs.sortedWith { o1, o2 ->
            o1.first.toLong().compareTo(o2.first.toLong())
        }.reversed())
        val logList: ArrayList<Pair<String, Pair<String, String>>> = ArrayList(logs)
        val adapter = LogsAdapter(requireActivity(), logList)
        rv.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            rv.context,
            layoutManager.orientation
        )
        rv.layoutManager = layoutManager
        rv.addItemDecoration(dividerItemDecoration)

        val list = arrayOf(info, debug, error, crash, assert, warn, verbose)
        val completeTextView: MaterialAutoCompleteTextView = v.findViewById(R.id.completeTextView)
        completeTextView.setSimpleItems(list)
        completeTextView.setText(verbose, false)
        completeTextView.setOnItemClickListener { _, _, position, _ ->
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

    private class LogsAdapter(
        private val activity: Activity,
        val logs: List<Pair<String, Pair<String, String>>>
    ) :
        RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(activity).inflate(R.layout.log_item, parent, false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int = logs.size

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val pair = logs[position]
            val title =
                SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.UK).format(Date(pair.first.toLong()))

            holder.time.text = title
            holder.body.text = pair.second.first
            holder.type.text = pair.second.second
            if (pair.second.second == Logger.Companion.Type.ERROR.name || pair.second.second == Logger.Companion.Type.CRASH.name)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    holder.type.setTextColor(
                        activity.resources.getColor(
                            android.R.color.holo_red_light,
                            null
                        )
                    )
                else holder.type.setTextColor(activity.resources.getColor(android.R.color.holo_red_light))
            else
                holder.type.setTextColor(Ui.getAttrColor(activity, android.R.attr.textColorPrimary))

            holder.layout.setOnClickListener {
                BottomSheet(
                    title,
                    pair.second.first,
                    false
                ).show((activity as AppCompatActivity).supportFragmentManager, "")
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val time: TextView = v.findViewById(R.id.txt_time)
            val type: TextView = v.findViewById(R.id.txt_type)
            val body: TextView = v.findViewById(R.id.txt_body)
            val layout: ViewGroup = v.findViewById(R.id.layout)
        }
    }

    class BottomSheet(
        private val title: String,
        private val message: String,
        private val roundedCorners: Boolean
    ) :
        BottomSheetDialogFragment() {
        private lateinit var scrollView: NestedScrollView

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return LinearLayout(context).apply {
                val backgroundColor = getAttrColor(context, android.R.attr.windowBackground)
                if (roundedCorners) {
                    setBackgroundResource(R.drawable.round_corners)
                    val drawable = background as GradientDrawable
                    drawable.setColor(backgroundColor)
                } else {
                    setBackgroundColor(backgroundColor)
                }
                orientation = VERTICAL
                addView(TextView(context).apply {
                    text = title
                    setPadding(5.dp(context))
                    setTextSize(COMPLEX_UNIT_SP, 18F)
                })
                addView(NestedScrollView(context).apply {
                    orientation = VERTICAL
                    scrollView = this
                    addView(LinearLayout(context).apply {
                        orientation = VERTICAL
                        addView(Button(context).apply {
                            text = getString(R.string.share_crash_url)
                            val typedArrayDark = requireActivity().obtainStyledAttributes(
                                intArrayOf(android.R.attr.selectableItemBackground)
                            )
                            background = typedArrayDark.getDrawable(0)
                            typedArrayDark.recycle()
                            setOnClickListener {
                                DogbinUtils.upload(
                                    message,
                                    object : DogbinUtils.UploadResultCallback {
                                        override fun onSuccess(url: String) {
                                            this@BottomSheet.dismiss()
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, url)
                                                type = "text/plain"
                                            }
                                            startActivity(
                                                Intent.createChooser(
                                                    sendIntent,
                                                    getString(R.string.send_to)
                                                )
                                            )
                                        }

                                        override fun onFail(message: String, e: Exception) {
                                            dialog?.dismiss()
                                            Logger.log(
                                                Logger.Companion.Type.ERROR,
                                                "Dogbin Upload",
                                                Log.getStackTraceString(e)
                                            )
                                        }
                                    })
                            }
                        })
                        message.split("\n").forEachIndexed { index, s ->
                            addView(TextView(context).apply {
                                text = s
                                setPadding(5.dp(context))
                                val typedArrayDark = requireActivity().obtainStyledAttributes(
                                    intArrayOf(android.R.attr.selectableItemBackground)
                                )
                                background = typedArrayDark.getDrawable(0)
                                typedArrayDark.recycle()
                                isFocusable = true
                                isClickable = true
                                setTextSize(COMPLEX_UNIT_SP, 14F)
                                setOnClickListener {
                                    LineBottomSheet(
                                        "${getString(R.string.copy_line)} ${index + 1}",
                                        s,
                                        index
                                    ).show(requireActivity().supportFragmentManager, "")
                                }
                            })
                        }
                    })
                })
            }
        }

        class LineBottomSheet(
            private val title: String,
            private val message: String,
            private val index: Int
        ) :
            BottomSheetDialogFragment() {
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                return LinearLayout(context).apply {
                    orientation = VERTICAL
                    setBackgroundColor(getAttrColor(context, android.R.attr.windowBackground))
                    addView(TextView(context).apply {
                        text = message
                        setPadding(7.dp(context))
                    })
                    addView(LinearLayout(context).apply {
                        setPadding(7.dp(context))
                        addView(Button(context).apply {
                            text = title
                            val typedArrayDark = requireActivity().obtainStyledAttributes(
                                intArrayOf(android.R.attr.selectableItemBackground)
                            )
                            background = typedArrayDark.getDrawable(0)
                            typedArrayDark.recycle()
                            setOnClickListener {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(title, message)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    "${getString(R.string.copied)} ${index + 1}",
                                    Toast.LENGTH_LONG
                                ).show()
                                this@LineBottomSheet.dismiss()
                            }
                        })
                    })
                }
            }
        }

        companion object {
            private fun getAttrColor(context: Context, attr: Int): Int {
                return try {
                    val ta = context.obtainStyledAttributes(intArrayOf(attr))
                    val colorAccent = ta.getColor(0, 0)
                    ta.recycle()
                    colorAccent
                } catch (e: Exception) {
                    Color.WHITE
                }
            }
        }
    }
}
