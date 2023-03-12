package com.yohbmeetup.yohb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.yohbmeetup.yohb.R;
import com.yohbmeetup.yohb.models.AttachmentTypes;
import com.yohbmeetup.yohb.models.Contact;
import com.yohbmeetup.yohb.models.Group;
import com.yohbmeetup.yohb.models.LogCall;
import com.yohbmeetup.yohb.models.Message;
import com.yohbmeetup.yohb.models.MyString;
import com.yohbmeetup.yohb.models.User;
import com.yohbmeetup.yohb.services.SinchService;
import com.yohbmeetup.yohb.utils.AudioPlayer;
import com.yohbmeetup.yohb.utils.Helper;
import com.yohbmeetup.yohb.viewHolders.BaseMessageViewHolder;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class IncomingVideoCallActivity extends BaseActivity {
    private String mCallId;
    private Long time;
    private String recipientId;
    private boolean isGroup;
    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incoming_call_screen);
        TextView callType = findViewById(R.id.yoohoo_calling);
        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();

        callType.setText("Video Calling");

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 30000);

        Intent intent = getIntent();
        mCallId = intent.getStringExtra(SinchService.CALL_ID);
        time = intent.getLongExtra("time", 0L);
        recipientId = intent.getStringExtra("recipient_id");

        isGroup = recipientId.startsWith(Helper.GROUP_PREFIX);

        if(!isGroup)
            user = rChatDb.where(User.class).equalTo("id", mCallId).findFirst();

        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
            = new JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("chat.enabled", false)
            .setWelcomePageEnabled(false)
            .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);


        findViewById(R.id.answerButton).setOnClickListener(mClickListener);
        findViewById(R.id.declineButton).setOnClickListener(mClickListener);

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAudioPlayer.stopRingtone();
            saveLog(true, "IN");
            switch (v.getId()) {
                case R.id.answerButton:
                    JitsiMeetUserInfo info = new JitsiMeetUserInfo();
                    info.setDisplayName("Sathi");
                    JitsiMeetConferenceOptions options
                        = new JitsiMeetConferenceOptions.Builder()
                        .setFeatureFlag("chat.enabled", false)
                        .setUserInfo(info)
                        .setRoom(mCallId + " " + time)
                        .build();
                    // Launch the new activity with the given options. The launch() method takes care
                    // of creating the required Intent and passing the options.
                    JitsiMeetActivity.launch(IncomingVideoCallActivity.this, options);
                    finish();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };

    private void declineClicked() {
        if(!isGroup){
            declineCall();
        }
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    void myUsersResult(ArrayList<User> myUsers) {

    }

    @Override
    void myContactsResult(HashMap<String, Contact> myContacts) {

    }

    @Override
    void userAdded(User valueUser) {

    }

    @Override
    void groupAdded(Group valueGroup) {

    }

    @Override
    void userUpdated(User valueUser) {

    }

    @Override
    void groupUpdated(Group valueGroup) {

    }

    @Override
    void onSinchConnected() {

    }

    @Override
    void onSinchDisconnected() {

    }

    private void declineCall() {
        String userOrGroupId = user != null ? user.getId() : group.getId();
        String chatChild = user != null ? Helper.getChatChild(user.getId(), userMe.getId()) : group.getId();
        Message message = new Message();
        message.setAttachmentType(AttachmentTypes.CALL_ENDED);
        BaseMessageViewHolder.animate = true;
        message.setBody("Call ended");
        message.setDate(System.currentTimeMillis());
        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSenderStatus(userMe.getStatus());
        message.setSenderImage(userMe.getImage());
        message.setSent(true);
        message.setDelivered(false);
        message.setRecipientId(userOrGroupId);
        message.setRecipientGroupIds(group != null ? new ArrayList<MyString>(group.getUserIds()) : null);
        message.setRecipientName(user != null ? user.getName() : group.getName());
        message.setRecipientImage(user != null ? user.getImage() : group.getImage());
        message.setRecipientStatus(user != null ? user.getStatus() : group.getStatus());
        message.setId(chatRef.child(chatChild).push().getKey());

        //Add message in chat child
        chatRef.child(chatChild).child(message.getId()).setValue(message);
        //Add message in recipient's inbox
        inboxRef.child(userOrGroupId).setValue(message);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioPlayer.stopRingtone();
    }

    private void saveLog(boolean isVideo, String inOrOut) {
        rChatDb.beginTransaction();
        rChatDb.copyToRealm(new LogCall(user, System.currentTimeMillis(), 0, isVideo, inOrOut, userMe.getId()));
        rChatDb.commitTransaction();
    }

}
