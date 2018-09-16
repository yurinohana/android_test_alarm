package com.example.yuria.alarm

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager.LayoutParams.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity()
// インタフェースの実装
, SimpleAlertDialog.OnClickListener
, DatePickerFragment.OnDateSelectedListener
, TimePickerFragment.OnTimeSelectedListener{
    // "起きる"が押されたとき
    override fun onPositiveClick() {
        finish()
    }
    // "あと5分"が押されたとき
    override fun onNegativeClick() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 5)
        setAlarmManager(calendar)
        finish()
    }
    // 日付選択された時
    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        // 選択された日付をテキストビューに表示
        dateText.text = android.text.format.DateFormat.format("yyyy/MM/dd", c)
    }
    // 時間選択された時
    override fun onSelected(hourOfDay: Int, minute: Int) {
        // 選択された時間をテキストビューに表示
        timeText.text = "%1$02d:%2$02d".format(hourOfDay, minute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AlarmBroadcastReceiverから受け取ったonReceiveがtrueだったらダイアログを表示
        if (intent?.getBooleanExtra("onReceive", false) == true) {
            // アプリがスリープ中でもダイアログを表示
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                        window.addFlags(FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED)
                else ->
                        window.addFlags(FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED or FLAG_DISMISS_KEYGUARD)
            }
            val dialog = SimpleAlertDialog()
            // onReceiveに関連づいたfragmentインスタンスを取得
            dialog.show(supportFragmentManager, "alert_dialog")
        }

        setContentView(R.layout.activity_main)

        // setAlarmボタンが押されたとき
        setAlarm.setOnClickListener {
            // 各テキストビューに表示されている日付と時間をDate型に変換
            val date = "${dateText.text} ${timeText.text}".toDate()
            // 日付が選択されていればその時刻でアラームをセット
            when {
                date != null -> {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    setAlarmManager(calendar)
                    toast("アラームをセットしました")
                }
                else -> {
                    toast("日付の形式が正しくありません")
                }
            }
        }
        // cancelAlarmボタンが押されたとき
        cancelAlarm.setOnClickListener {
            cancelAlarmManager()
        }
        // 日付選択が押されたときのダイアログ表示
        dateText.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "date_dialog")
        }
        // 時間選択が押されたときのダイアログ表示
        timeText.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    // アラームをセットするメソッド
    private fun setAlarmManager(calendar: Calendar) {
        // AlarmManagerクラスのインスタンス．Any型をAlarmManagerへと変換
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // アラーム時刻になったときのインテントを作成
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        // インテントを指定してペンディングインテントを作成（指定したタイミングでインテントを発行する場合は必要）
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        when {
            // Lollipop以上
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                // Alarmのスケジュール情報
                val info = AlarmManager.AlarmClockInfo(
                        calendar.timeInMillis, null)
                // 上記とアラーム時刻になったときに実行するインテントをセット
                am.setAlarmClock(info, pending)
            }
        }
    }

    // アラームをキャンセルするメソッド
    private fun cancelAlarmManager() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        am.cancel(pending)
    }

    // Date型へ変換するメソッド
    fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalAccessException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
        return date
    }
}
