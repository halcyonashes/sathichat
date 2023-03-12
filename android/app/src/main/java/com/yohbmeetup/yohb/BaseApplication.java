package com.yohbmeetup.yohb;

import android.app.Application;
import android.content.Context;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.yohbmeetup.yohb.receivers.ConnectivityReceiver;


public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectivityReceiver.init(this);
//        Fabric.with(this, new Crashlytics());
        EmojiManager.install(new GoogleEmojiProvider());

    }
}
