package com.yohbmeetup.yohb.viewHolders;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.yohbmeetup.yohb.activities.IncomingAudioCallActivity;
import com.yohbmeetup.yohb.activities.IncomingVideoCallActivity;
import com.yohbmeetup.yohb.interfaces.OnMessageItemClick;
import com.yohbmeetup.yohb.models.AttachmentTypes;
import com.yohbmeetup.yohb.models.Message;
import com.yohbmeetup.yohb.utils.Helper;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.yohbmeetup.yohb.services.SinchService.CALL_ID;

/**
 * Created by a_man on 5/11/2017.
 */

public class MessageAudioCallViewHolder extends BaseMessageViewHolder {
    private String userId;
    private Long time;
    private int totalSize;
    private int currentPosition;

    public MessageAudioCallViewHolder(View itemView, OnMessageItemClick itemClickListener) {
        super(itemView, itemClickListener);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper helper = new Helper(context);
                JitsiMeetUserInfo info = new JitsiMeetUserInfo();
                info.setDisplayName("Sathi");
                JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(userId + " " + time)
                    .setFeatureFlag("chat.enabled", false)
                    .setUserInfo(info)
                    .setAudioOnly(true)
                    .build();
                // Launch the new activity with the given options. The launch() method takes care
                // of creating the required Intent and passing the options.

                if(totalSize > 2){
                    if(currentPosition + 2 >= totalSize)
                        JitsiMeetActivity.launch(context, options);
                }
                else
                    JitsiMeetActivity.launch(context, options);
            }
        });

    }

    @Override
    public void setData(Message message, int position, int size) {
        super.setData(message, position, size);
        userId = message.getSenderId();
        time = message.getDate();
        totalSize = size;
        currentPosition = position;
        int messageType = message.getAttachmentType();

        long currentTime = System.currentTimeMillis();
        if(messageType == AttachmentTypes.AUDIO_CALL && (position + 1) == size && (message.getDate() + 15000) > currentTime && !isMine()) {
            Intent intent = new Intent(context, IncomingAudioCallActivity.class);
            intent.putExtra(CALL_ID, userId);
            intent.putExtra("time", time);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

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
            .setWelcomePageEnabled(false)
            .setFeatureFlag("chat.enabled", false)
            .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
    }

}
