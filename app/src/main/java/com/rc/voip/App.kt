package com.rc.voip

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}