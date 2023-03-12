package com.yohbmeetup.yohb.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.yohbmeetup.yohb.models.User;
import com.yohbmeetup.yohb.utils.Helper;

public class SinchService extends Service {
    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private String mUserId;

    private StartFailedListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Helper helper = new Helper(this);
        User userMe = helper.getLoggedInUser();
        if (User.validate(userMe)) {
            start(userMe.getId());
        }
    }


    private void start(String userName) {

    }

    private void stop() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

    }

    public interface StartFailedListener {

        void onStarted();
    }


}