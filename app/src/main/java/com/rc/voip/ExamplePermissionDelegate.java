package com.rc.voip;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import org.mozilla.geckoview.GeckoSession;

public class ExamplePermissionDelegate implements GeckoSession.PermissionDelegate {

    private Context context;

    public ExamplePermissionDelegate(Context context) {
        this.context = context;
    }

    @Override
    public void onAndroidPermissionsRequest(final GeckoSession session,
                                            final String[] permissions,
                                            final Callback callback) {
    }

    @Override
    public void onMediaPermissionRequest(final GeckoSession session,
                                         final String uri,
                                         final MediaSource[] video,
                                         final MediaSource[] audio,
                                         final MediaCallback callback) {
        if ((audio != null
                && ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            callback.reject();
            return;
        }
        if (audio != null ) {
            callback.grant(null,audio[0]);
        }


    }
}