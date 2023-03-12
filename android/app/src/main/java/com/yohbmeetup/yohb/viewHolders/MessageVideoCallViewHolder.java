package com.yohbmeetup.yohb.viewHolders;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.yohbmeetup.yohb.activities.IncomingVideoCallActivity;
import com.yohbmeetup.yohb.interfaces.OnMessageItemClick;
import com.yohbmeetup.yohb.models.AttachmentTypes;
import com.yohbmeetup.yohb.models.Message;
import com.yohbmeetup.yohb.utils.Helper;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import static com.yohbmeetup.yohb.services.SinchService.CALL_ID;

/**
 * Created by a_man on 5/11/2017.
 */

public class MessageVideoCallViewHolder extends BaseMessageViewHolder {
    private String userId;
    private Long time;
    private int totalSize;
    private int currentPosition;
    private boolean isGroup;

    public MessageVideoCallViewHolder(View itemView, OnMessageItemClick itemClickListener) {
        super(itemView, itemClickListener);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(userId + " " + time)
                    .setFeatureFlag("chat.enabled", false)
                    .build();

                if(totalSize > 2 && !isGroup){
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
        isGroup = message.getRecipientId().startsWith(Helper.GROUP_PREFIX);
        int messageType = message.getAttachmentType();

        long currentTime = System.currentTimeMillis();
        if(messageType == AttachmentTypes.VIDEO_CALL && (position + 1) == size && (message.getDate() + 15000) > currentTime && !isMine()) {
            Intent intent = new Intent(context, IncomingVideoCallActivity.class);
            intent.putExtra(CALL_ID, userId);
            intent.putExtra("time", time);
            intent.putExtra("recipient_id", message.getRecipientId());
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
            .setFeatureFlag("chat.enabled", false)
            .setWelcomePageEnabled(false)
            .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
    }

}
