package com.example.yuria.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast

class AlarmBroadcastReceiver: BroadcastReceiver() {
    // Alarmマネージャからインテントを受け取った時呼び出されるメソッド
    override fun onReceive(context: Context?, intent: Intent?) {
        // アクティビティを呼び出すためのインテントの作成
        context?.run {
            startActivity(
                    intentFor<MainActivity>("onReceive" to true).newTask()
            )
        }
    }
}