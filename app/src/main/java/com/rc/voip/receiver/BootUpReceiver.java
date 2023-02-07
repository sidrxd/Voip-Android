package com.rc.voip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rc.voip.VoipActivity;


public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(this.getClass().getSimpleName(), "In On Receive method.");
            Intent main = new Intent(context, VoipActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(main);
        }
    }

}
