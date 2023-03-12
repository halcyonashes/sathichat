package com.yohbmeetup.yohb.activities;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.Nullable;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class JitsiAudioActivity extends JitsiMeetActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE
            = (int) (Math.random() * Short.MAX_VALUE);


    @Override
    protected void initialize() {
        // Set default options
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setWelcomePageEnabled(true)
                .setServerURL(buildURL("https://meet.jit.si"))
                .setFeatureFlag("call-integration.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        super.initialize();
    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {
        Log.d("Conference", "Conference Joined");
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> data) {
        Log.d("Conference", "Conference terminated: " + data);
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {
        Log.d("Conference", "Conference Will Join");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE
                && canRequestOverlayPermission()) {
            if (Settings.canDrawOverlays(this)) {
                initialize();
                return;
            }

            throw new RuntimeException("Overlay permission is required when running in Debug mode.");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private @Nullable URL buildURL(String urlStr) {
        try {
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean canRequestOverlayPermission() {
        return
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M;
    }

}
