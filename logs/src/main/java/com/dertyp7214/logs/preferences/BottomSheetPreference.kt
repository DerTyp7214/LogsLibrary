/*
 * Copyright (c) 2019.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.logs.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.preference.ListPreference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.logs.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BottomSheetPreference : ListPreference {
    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        this.init()
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.init()
    }

    constructor(context: Context) : super(context) {
        this.init()
    }

    open fun init() {
        setDefaultValue("VERBOSE")
        entries = arrayOf("VERBOSE", "ASSERT", "INFO", "WARN", "DEBUG", "ERROR", "CRASH")
        entryValues = arrayOf("VERBOSE", "ASSERT", "INFO", "WARN", "DEBUG", "ERROR", "CRASH")
    }

    override fun onClick() {
        openBottomSheet()
    }

    private fun openBottomSheet() {
        val list = ArrayList<Pair<String, String>>()
        val entryList = ArrayList<String>()
        val entryValueList = ArrayList<String>()
        if (entries != null && entryValues != null && entries.isNotEmpty() && entryValues.isNotEmpty()) {
            entries.iterator().forEach { entryList.add(it.toString()) }
            entryValues.iterator().forEach { entryValueList.add(it.toString()) }
            if (entryList.size == entryValueList.size) {
                entryList.forEachIndexed { index, s ->
                    list.add(Pair(s, entryValueList[index]))
                }
            }
            BottomSheet(
                list,
                entryValueList.indexOf(value)
            ) {
                value = it
                if (onPreferenceChangeListener != null) onPreferenceChangeListener.onPreferenceChange(this, it)
            }.show(scanForActivity(context)!!.supportFragmentManager, "")
        } else {
            Toast.makeText(context, R.string.no_entries, Toast.LENGTH_LONG).show()
        }
    }

    private fun scanForActivity(cont: Context?): AppCompatActivity? {
        return when (cont) {
            null -> null
            is AppCompatActivity -> cont
            is ContextWrapper -> scanForActivity(cont.baseContext)
            else -> null
        }
    }

    class BottomSheet(
        private val list: ArrayList<Pair<String, String>>,
        private val selectedIndex: Int,
        private val clickListener: (element: String) -> Unit
    ) :
        BottomSheetDialogFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return LinearLayout(context).apply {
                setBackgroundColor(getAttrColor(context, android.R.attr.windowBackground))
                orientation = LinearLayout.VERTICAL
                setPadding(dpToPx(context, 5F).toInt())
                addView(RecyclerView(context!!).apply {
                    adapter = Adapter(context!!, list, selectedIndex) {
                        clickListener(it)
                        this@BottomSheet.dismiss()
                    }
                    layoutManager = LinearLayoutManager(context)
                    setPadding(0, dpToPx(context, 7F).toInt(), 0, dpToPx(context, 7F).toInt())
                })
            }
        }

        companion object {
            private fun dpToPx(context: Context, dp: Float): Float {
                val scale = context.resources.displayMetrics.density
                return (dp * scale + 0.5f)
            }

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

        private class Adapter(
            private val context: Context,
            private val list: ArrayList<Pair<String, String>>,
            private val selectedIndex: Int,
            private val clickListener: (element: String) -> Unit
        ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val button = RadioButton(context)
                button.id = R.id.bottomSheetPreferenceItemId
                button.setPadding(dpToPx(context, 8F).toInt())
                button.setTextSize(COMPLEX_UNIT_SP, 18F)
                return ViewHolder(button)
            }

            override fun getItemCount(): Int = list.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val pair = list[position]

                holder.button.text = pair.second
                holder.button.isChecked = position == selectedIndex
                holder.button.setOnClickListener {
                    clickListener(pair.first)
                }
            }

            class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
                val button: RadioButton = v.findViewById(R.id.bottomSheetPreferenceItemId)
            }
        }
    }
}
