package com.yohbmeetup.yohb.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yohbmeetup.yohb.models.AttachmentTypes;
import com.yohbmeetup.yohb.models.Message;
import com.yohbmeetup.yohb.models.User;
import com.yohbmeetup.yohb.utils.Helper;
import com.yohbmeetup.yohb.viewHolders.BaseMessageViewHolder;



public class ActionReceiver extends BroadcastReceiver {
    protected DatabaseReference chatRef, inboxRef;
    private static final String CHANNEL_ID_GROUP = "my_channel_02";
    private static final String CHANNEL_ID_USER = "my_channel_03";

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        chatRef = firebaseDatabase.getReference(Helper.REF_CHAT);
        inboxRef = firebaseDatabase.getReference(Helper.REF_INBOX);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        Message message = intent.getParcelableExtra("message");

        int msgId = 0;
        try {
            msgId = Integer.parseInt(message.getSenderId());
        } catch (NumberFormatException ex) {
            msgId = Integer.parseInt(message.getSenderId().substring(message.getSenderId().length() / 2));
        }

        if (remoteInput != null) {
            String inputString = remoteInput.getCharSequence(
                "key_text_reply").toString();
            Log.d("Notification", inputString);

            sendMessage(message, inputString, msgId, context);
        }

    }

    private void sendMessage(Message receivedMessage, String input, int msgId, Context context) {
        boolean isGroup;
        isGroup = receivedMessage.getRecipientId().startsWith(Helper.GROUP_PREFIX);
        String userOrGroupId, chatChild;
        Helper helper = new Helper(context);
        User userMe = helper.getLoggedInUser();
        if(isGroup) {
            userOrGroupId = receivedMessage.getRecipientId();
            chatChild = receivedMessage.getRecipientId();
        }
        else {
            userOrGroupId = receivedMessage.getSenderId();
            chatChild = Helper.getChatChild(receivedMessage.getSenderId(), receivedMessage.getRecipientId());

        }

        Message message = new Message();
        message.setAttachmentType(AttachmentTypes.NONE_TEXT);
        BaseMessageViewHolder.animate = true;
        message.setBody(input);
        message.setDate(System.currentTimeMillis());

        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSenderStatus(userMe.getStatus());
        message.setSenderImage(userMe.getImage());
        message.setSent(true);
        message.setDelivered(false);
        message.setRecipientId(userOrGroupId);
        message.setRecipientGroupIds(null);
        message.setRecipientName(receivedMessage.getSenderName());
        message.setRecipientImage(receivedMessage.getSenderImage());
        message.setRecipientStatus(receivedMessage.getSenderStatus());
        message.setId(chatRef.child(chatChild).push().getKey());

        //Add message in chat child
        chatRef.child(chatChild).child(message.getId()).setValue(message);
        //Add message in recipient's inbox
        inboxRef.child(userOrGroupId).setValue(message);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        String channelId = isGroup ? CHANNEL_ID_GROUP : CHANNEL_ID_USER;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Sathichat new message notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(context, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        notificationBuilder.setSmallIcon(
            android.R.drawable.ic_dialog_info)
            .setContentText("Reply sent")
            .build();

        notificationManager.notify(msgId, notificationBuilder.build());

    }


}