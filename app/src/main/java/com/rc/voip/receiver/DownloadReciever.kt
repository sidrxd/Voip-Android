package com.rc.voip.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.SyncStateContract
import android.util.Log
import com.rc.voip.Constants
import com.rc.voip.InstallerActivity
import io.github.solrudev.simpleinstaller.apksource.UriApkSource
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.SessionOptions
import java.io.File

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Constants.ACTION_DOWNLOAD_COMPLETE) {
            Log.d("DownloadReceiver", "onReceive: " + " download complete")
//            val installerActivity = Intent(context, InstallerActivity::class.java)
//            installerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(installerActivity)

            val folder =
                File(Environment.getExternalStorageDirectory().toString() + "/Download/jcupdate")
            if (!folder.exists()) folder.mkdir()
            if (folder.exists()) {
                val files = folder.listFiles()
                if (files != null) {
                    // install(files[0])
                    installFromStorage(files[0])
//                installCoroutine(Uri.fromFile(
//                    files[0]
//                ))
                }
            }
        }
    }

    private fun installFromStorage(file: File) {
        val apkSource = UriApkSource(Uri.fromFile(file))
        val sessionOptions = SessionOptions {
            this.setConfirmationStrategy(ConfirmationStrategy.IMMEDIATE)
        }
        val result =
            io.github.solrudev.simpleinstaller.PackageInstaller.installPackage(apkFile = apkSource,
                sessionOptions,
                object : io.github.solrudev.simpleinstaller.PackageInstaller.Callback {
                    override fun onSuccess() {
                        super.onSuccess()
                    }
                })

    }
}