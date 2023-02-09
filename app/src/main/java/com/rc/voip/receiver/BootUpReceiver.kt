package com.rc.voip.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rc.voip.VoipActivity

class BootUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(this.javaClass.simpleName, "In On Receive method.")
            val main = Intent(context, VoipActivity::class.java)
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(main)
        }
    }
}