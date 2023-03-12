package com.yohbmeetup.yohb.viewHolders;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.yohbmeetup.yohb.R;
import com.vanniktech.emoji.EmojiTextView;
import com.yohbmeetup.yohb.interfaces.OnMessageItemClick;
import com.yohbmeetup.yohb.models.Message;
import com.yohbmeetup.yohb.utils.AESEncryption;
import com.yohbmeetup.yohb.utils.GeneralUtils;
import com.yohbmeetup.yohb.utils.LinkTransformationMethod;

import java.security.GeneralSecurityException;

import static com.yohbmeetup.yohb.adapters.MessageAdapter.MY;

/**
 * Created by mayank on 11/5/17.
 */

public class MessageTextViewHolder extends BaseMessageViewHolder {
    EmojiTextView text;
    LinearLayout ll;

    private Message message;
    private String decryptedMessage;

    private static int _4dpInPx = -1;

    public MessageTextViewHolder(View itemView, View newMessageView, OnMessageItemClick itemClickListener) {
        super(itemView, newMessageView, itemClickListener);
        text = itemView.findViewById(R.id.text);
        ll = itemView.findViewById(R.id.container);

        text.setTransformationMethod(new LinkTransformationMethod());
        text.setMovementMethod(LinkMovementMethod.getInstance());
        if (_4dpInPx == -1) _4dpInPx = GeneralUtils.dpToPx(itemView.getContext(), 4);
        itemView.setOnClickListener(v -> onItemClick(true));

        itemView.setOnLongClickListener(v -> {
            onItemClick(false);
            return true;
        });
    }

    @Override
    public void setData(Message message, int position, int size) {
        super.setData(message, position, size);
        this.message = message;
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, message.isSelected() ? R.color.colorPrimary : R.color.colorBgLight));
        ll.setBackgroundColor(message.isSelected() ? ContextCompat.getColor(context, R.color.colorPrimary) : isMine() ? Color.WHITE : ContextCompat.getColor(context, R.color.colorBgLight));
        try {
            Log.d("Encrypted", "Recipient ID: " + message.getRecipientId());
            decryptedMessage = AESEncryption.decrypt(message.getRecipientId(), message.getBody());
        }
        catch (Exception e){
            decryptedMessage = message.getBody();
        }

        text.setText(decryptedMessage);
        if (getItemViewType() == MY) {
            animateView(position);
        }
    }

    private void animateView(int position) {
        if (animate && position > lastPosition) {

            itemView.post(new Runnable() {
                @Override
                public void run() {

                    float originalX = cardView.getX();
                    final float originalY = itemView.getY();
                    int[] loc = new int[2];
                    newMessageView.getLocationOnScreen(loc);
                    cardView.setX(loc[0] / 2);
                    itemView.setY(loc[1]);
                    ValueAnimator radiusAnimator = new ValueAnimator();
                    radiusAnimator.setFloatValues(80, _4dpInPx);
                    radiusAnimator.setDuration(850);
                    radiusAnimator.addUpdateListener(animation -> cardView.setRadius((Float) animation.getAnimatedValue()));
                    radiusAnimator.start();
                    cardView.animate().x(originalX).setDuration(900).setInterpolator(new DecelerateInterpolator()).start();
                    itemView.animate().y(originalY - _4dpInPx).setDuration(750).setInterpolator(new DecelerateInterpolator()).start();
                    new Handler().postDelayed(() -> itemView.animate().y(originalY + _4dpInPx).setDuration(250).setInterpolator(new DecelerateInterpolator()).start(), 750);
                }
            });
            lastPosition = position;
//            setAnimation(getAdapterPosition());
            animate = false;
        }
    }

}
