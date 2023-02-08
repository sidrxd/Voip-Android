package com.rc.voip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.rc.voip.databinding.ActivityVoipBinding


class VoipActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1
        const val URL = "https://jc400-audio.web.app/?token=af5db706-43d2-405a-9f13-965b86a6f39c"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityVoipBinding
    private var myRequest: PermissionRequest? = null
    private var webView : WebView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWebView()
        binding.include.xReload.setOnClickListener {
            webView?.loadUrl(URL)
        }
        binding.include.xBtnOpen.setOnClickListener {
            openInBrowser()
        }
    }

    private fun setWebView() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        am.isSpeakerphoneOn = true
        webView = WebView(applicationContext)
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.settings?.mediaPlaybackRequiresUserGesture = false;

        webView?.webViewClient = WebViewClient()
        webView?.settings?.saveFormData = true
        webView?.settings?.setSupportZoom(false)
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
                                Manifest.permission.RECORD_AUDIO,
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
                            )
                        }
                    }
                }
            }
        }

        webView?.loadUrl(URL)

        //webView.loadUrl("https://jc400-audio.web.app?token=af5db706-43d2-405a-9f13-965b86a6f39c")
    }

    override fun onBackPressed() {
        if (webView?.canGoBack()==true) {
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