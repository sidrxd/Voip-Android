package com.rc.voip

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.rc.voip.receiver.BootUpReceiver
import io.github.solrudev.simpleinstaller.apksource.UriApkSource
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.data.SessionOptions
import java.io.File


class InstallerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installer)
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
        this.finish()
    }


    private fun install(file: File) {
        Log.d("weasd", "2 install file=" + file)
        val intent = Intent("com.jimi.apkinstall")
        intent.putExtra("com.jimi.apkinstall.pkg", "com.jimi.serialport")
        intent.putExtra("com.jimi.apkinstall.path", file)
        sendBroadcast(intent)
    }

    private fun installUsingCmd(file: File) {
        Log.d("TAG", "installUsingCmd: $file")
        val intent = Intent("com.jimi.cmd.exe");
        intent.putExtra("cmd", "pm install -i com.jimi.serialport -r $file")
        sendBroadcast(intent)
    }


    private fun installCoroutine(apkUri: Uri) {


        val installer = this.packageManager.packageInstaller
        val resolver = this.contentResolver
        resolver.openInputStream(apkUri)?.use { apkStream ->
            val length =
                DocumentFile.fromSingleUri(getApplication(), apkUri)?.length() ?: -1
            val params =
                PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            val sessionId = installer.createSession(params)
            val session = installer.openSession(sessionId)

            session.openWrite("rc_voice", 0, length).use { sessionStream ->
                apkStream.copyTo(sessionStream)
                session.fsync(sessionStream)
            }

            val intent = Intent(applicationContext, BootUpReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                applicationContext,
                236,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            session.commit(pi.intentSender)
            session.close()
        }
    }
}