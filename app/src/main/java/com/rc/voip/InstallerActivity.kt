package com.rc.voip

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.PointerIconCompat
import androidx.documentfile.provider.DocumentFile
import com.rc.voip.receiver.BootUpReceiver
import io.github.solrudev.simpleinstaller.apksource.UriApkSource
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.SessionOptions
import java.io.File


class InstallerActivity : AppCompatActivity(), ServiceConnection {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installer)
        bindService()

//        val folder =
//            File( "/storage/emulated/0/Download/jcupdate/")
////            File(Environment.DIRECTORY_DOWNLOADS + "/jcupdate/")
//        if (!folder.exists()) folder.mkdir()
//        Log.d("ipath", folder.absolutePath)
//
//        if (folder.exists()) {
//            val files = folder.listFiles()
//            Log.d("countf", files.size.toString())
//
//            if (files != null) {
//                Log.d("apk_path", files[0].name)
//                install(files[0])
//            }
//        }
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


    fun connection() = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    var messenger: Messenger? = null

    private fun installDemoCode(file: File) {
        val path = Environment.getExternalStorageDirectory().getAbsolutePath();
        val f = File(path + "/tttesttt/demo2.apk");
        Log.d("weasd", "f=" + f.getAbsolutePath() + "  " + f.exists());
        if (f.exists() && f.isFile()) {
            try {
                val pkg = packageName
                val m = Message.obtain()
                val b = Bundle();
                b.putString("pkg", pkg);
                b.putString("path", f.absolutePath);
                m.data = b;
                this.messenger?.send(m);
                Log.d("weasd", "  set time");
            } catch (e: java.lang.Exception) {
                Log.d("weasd", "  set time  e=" + e);
            }
        }
    }

    private fun openInBrowser() {
//        val folder =
//            File( "/storage/emulated/0/Download/jcupdate/")
////            File(Environment.DIRECTORY_DOWNLOADS + "/jcupdate/")
//        if (!folder.exists()) folder.mkdir()
//        Log.d("ipath", folder.absolutePath)
//
//        if (folder.exists()) {
//            val files = folder.listFiles()
//            Log.d("countf", files.size.toString())
//
//            if (files != null) {
//                Log.d("apk_path", files[0].name)
//                install(files[0])
//            }
//        }
        install(null)
    }

    fun installApk(f: File) {
        try {
            if (messenger != null) {
                val m: Message =
                    Message.obtain(null as Handler?, PointerIconCompat.TYPE_CONTEXT_MENU)
                val b = Bundle()
                b.putString("path", f.absolutePath)
                m.data = b
                messenger!!.send(m)
            }
        } catch (_: Exception) {
        }
    }

    private fun install(file: File?) {
        var path = Environment.getExternalStorageDirectory().getAbsolutePath();
        var f = File(path + "/Download/jcupdate/app-release.apk")
        Log.d("weasd", "f=" + f.getAbsolutePath() + "  " + f.exists());
        if (f.exists() && f.isFile()) {
            installApk(f)
        }

        /* Log.d("weasd", "2 install file=" + file)
         val intent = Intent()
         intent.putExtra("com.jimi.apkupdater.pkg", "com.rc.voip")
         intent.putExtra("com.jimi.apkupdater.path", file.absolutePath)
         intent.action = "com.jimi.proxy.action"
         intent.component = ComponentName("com.jimi.apkupdater", "com.jimi.aidl.ProxyService")
         sendBroadcast(intent)*/
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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        messenger = Messenger(service)
        Log.d("servweasd", "onServiceConnected: ")
        openInBrowser()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    private fun bindService() {
        Log.d("weasd", " bindService messenger = " + this.messenger);
        if (this.messenger == null) {
            val intent = Intent()
            intent.action = "com.jimi.proxy.action";
            intent.component = ComponentName("com.jimi.apkupdater", "com.jimi.aidl.ProxyService");
            bindService(intent, this, BIND_AUTO_CREATE)
        }
    }
}