package com.example.yuria.alarm

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.*

// アラーム設定ダイアログ
class SimpleAlertDialog : DialogFragment() {

    // 処理はMainActivityで実装
    interface OnClickListener {
        // "起きる"
        fun onPositiveClick()
        // "あと5分"
        fun onNegativeClick()
    }

    private lateinit var listener: OnClickListener

    // フラグメントが呼ばれ最初にコンテキストにアタッチされた時に呼ばれるメソッド
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // SimpleAlertDialogのonClickListenerをlistenerに登録
        if (context is SimpleAlertDialog.OnClickListener) {
            listener = context
        }
    }

    // ダイアログが生成された時呼び出されるメソッド
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context
        if (context == null)
            return super.onCreateDialog(savedInstanceState)
        // 表示するダイアログの内容の設定
        val builder = AlertDialog.Builder(context).apply {
            setMessage("時間になりました！")
            setPositiveButton("起きる") {dialog, which ->
                listener.onPositiveClick()
            }
            setNegativeButton("あと5分") {dialog, which ->
                listener.onNegativeClick()
            }
        }
        return builder.create()
    }
}

// 日付選択ダイアログ
class DatePickerFragment : DialogFragment(),
        DatePickerDialog.OnDateSetListener {
    // MainActivityで実装
    interface OnDateSelectedListener {
        fun onSelected(year: Int, month: Int, date: Int)
    }

    private lateinit var listener: OnDateSelectedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnDateSelectedListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 現在の日付を初期値に設定
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)
        return DatePickerDialog(context, this, year, month, date)
    }
    // DatePickerDialogで日付が選択された時
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, date: Int) {
        listener.onSelected(year, month, date)
    }
}

// 時刻選択ダイアログ
class TimePickerFragment : DialogFragment(),
        TimePickerDialog.OnTimeSetListener {

    interface OnTimeSelectedListener {
        fun onSelected(hourOfDay: Int, minute: Int)
    }

    private lateinit var listener: OnTimeSelectedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TimePickerFragment.OnTimeSelectedListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(context, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        listener.onSelected(hourOfDay, minute)
    }
}