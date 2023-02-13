package com.rc.voip

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun getTodayDate():String{
        val sdf = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss")
        return  sdf.format(Date())

    }


    fun startDownload(
        downloadPath: String?,
        destinationPath: String,
        context: Context,
        FileName: String
    ) {
        Log.d("download path", downloadPath!!)
        val uri = Uri.parse(downloadPath)
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
        request.setTitle(FileName + "") // Title for notification.
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            destinationPath + FileName
        )
        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    }

    fun speech(context: Context,toSpeak : String){
        val tts =TextToSpeech(context) {

        }
        tts.language = Locale.US

        tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null)
    }

}