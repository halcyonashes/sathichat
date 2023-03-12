package com.yohbmeetup.yohb.models;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mayank on 10/5/17.
 */

public class AttachmentTypes {

    public static final int CONTACT = 0;
    public static final int VIDEO = 1;
    public static final int IMAGE = 2;
    public static final int AUDIO = 3;
    public static final int LOCATION = 4;
    public static final int DOCUMENT = 5;
    public static final int NONE_TEXT = 6;
    public static final int NONE_TYPING = 7;
    public static final int RECORDING = 8;
    public static final int VIDEO_CALL = 9;
    public static final int AUDIO_CALL = 10;
    public static final int CALL_ENDED = 11;

    @IntDef({CONTACT, VIDEO, IMAGE, AUDIO, LOCATION, DOCUMENT, NONE_TEXT, NONE_TYPING, RECORDING, VIDEO_CALL, AUDIO_CALL, CALL_ENDED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AttachmentType {
    }

    public static String getTypeName(@AttachmentType int attachmentType) {
        switch (attachmentType) {
            case AUDIO:
                return "Audio";
            case VIDEO:
                return "Video";
            case CONTACT:
                return "Contact";
            case DOCUMENT:
                return "Document";
            case IMAGE:
                return "Image";
            case LOCATION:
                return "Location";
            case NONE_TEXT:
                return "none_text";
            case NONE_TYPING:
                return "none_typing";
            case RECORDING:
                return "Recording";
            case VIDEO_CALL:
                return "video_call";
            case AUDIO_CALL:
                return "audio_call";
            case CALL_ENDED:
                return "call_ended";
            default:
                return "none";
        }
    }
}
