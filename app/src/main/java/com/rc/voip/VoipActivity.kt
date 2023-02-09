package com.rc.voip

import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.rc.voip.databinding.ActivityVoipBinding
import com.rc.voip.receiver.DownloadReceiver
import com.rc.voip.receiver.PermissionCheck
import java.io.File


class VoipActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1
        const val URL = "https://jc400-audio.web.app/?token=af5db706-43d2-405a-9f13-965b86a6f39c"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityVoipBinding
    private var myRequest: PermissionRequest? = null
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  setGecko()
        setWebView()
        binding.include.xReload.setOnClickListener {
            webView?.loadUrl(URL)
        }
        binding.include.xBtnOpen.setOnClickListener {
            openInBrowser()
        }
        startAutoUpdate()
    }

//    private fun setGecko() {
//        val view = binding.include.webView
//        val session = GeckoSession()
//        session.contentDelegate = object : GeckoSession.ContentDelegate {}
//
//        val runtime = GeckoRuntime.create(this)
//        runtime.settings.javaScriptEnabled = true
//        session.open(runtime)
//        view.setSession(session)
//        session.loadUri(URL) // Or any other URL...
//
//    }


    private fun startAutoUpdate() {
        val listener = object : AppUpdaterUtils.UpdateListener {
            override fun onSuccess(update: Update?, isUpdateAvailable: Boolean?) {
                Log.d("Latest Version", update?.latestVersion.toString())
                Log.d("Latest Version Code", update?.latestVersionCode.toString())
                Log.d("Release notes", update?.releaseNotes.toString())
                Toast.makeText(this@VoipActivity, "new update available", Toast.LENGTH_SHORT).show()


                val downloadUrl =
                    update?.urlToDownload.toString() + "/download/1.0.0.0/app-release.apk"
                val folder = File(
                    Environment.getExternalStorageDirectory().toString() + "/Download/jcupdate"
                )
                Log.d("URL", downloadUrl)

                deleteRecursive(folder)
                if (PermissionCheck.readAndWriteExternalStorage(this@VoipActivity)){
                    Utils.startDownload(
                        downloadUrl,
                        "jcupdate/",
                        applicationContext,
                        "app-release.apk"
                    )
                }


                // https://github.com/sidrxd/Voip-Android/releases/download/1.0.0.0/app-release.apk

            }

            override fun onFailed(error: AppUpdaterError?) {
                Log.d("TAG", "onFailed: ")
            }
        }

        val appUpdater = AppUpdaterUtils(this)
            // .setDisplay(Display.DIALOG)
            .withListener(listener)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("sidrxd", "Voip-Android")
            .setUpdateFrom(UpdateFrom.JSON)
            .setUpdateJSON("https://raw.githubusercontent.com/sidrxd/Voip-Android/master/app/update_changelog.json")
        appUpdater.start()

        registerReceiver(DownloadReceiver(),  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    private fun setWebView() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        am.isSpeakerphoneOn = true
        webView = binding.include.webView
        WebView.setWebContentsDebuggingEnabled(true)

        webView?.settings?.javaScriptEnabled = true

        if (webView?.settings?.javaScriptEnabled == true) {
            Log.e("TAG", "setWebView: javascript enabled")
        } else {
            Log.e(
                "TAG", "setWebView: javascript false" +
                        ""
            )

        }
        webView?.settings?.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
        webView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.settings?.mediaPlaybackRequiresUserGesture = false;
        webView?.settings?.domStorageEnabled = true
        webView?.webViewClient = WebViewClient()
        webView?.settings?.saveFormData = true
        webView?.settings?.setSupportZoom(true)
        webView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE
        webView?.settings?.pluginState = WebSettings.PluginState.ON
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                myRequest = request
                for (permission in request.resources) {
                    when (permission) {
                        "android.webkit.resource.AUDIO_CAPTURE" -> {
                            askForPermission(
                                request.origin.toString(),
                                android.Manifest.permission.RECORD_AUDIO,
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
                            )
                        }
                    }
                }
            }
        }
        Log.i("WebViewActivity", "UA: " + webView?.getSettings()?.getUserAgentString());

        webView?.loadUrl(URL)

        //webView.loadUrl("https://jc400-audio.web.app?token=af5db706-43d2-405a-9f13-965b86a6f39c")
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                Log.d("WebView", "PERMISSION FOR AUDIO")
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    myRequest?.grant(myRequest?.resources)
                    webView?.loadUrl(URL)
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }


    fun askForPermission(origin: String, permission: String, requestCode: Int) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@VoipActivity,
                    permission
                )
            ) {
            } else {

                ActivityCompat.requestPermissions(
                    this@VoipActivity, arrayOf(permission),
                    requestCode
                )
            }
        } else {
            myRequest?.grant(myRequest?.resources)
        }
    }


    private fun openInBrowser() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        startActivity(browserIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        webView?.destroy()
    }
}