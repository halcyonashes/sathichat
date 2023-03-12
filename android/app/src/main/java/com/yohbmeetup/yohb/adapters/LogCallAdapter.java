package com.yohbmeetup.yohb.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yohbmeetup.yohb.R;
import com.yohbmeetup.yohb.interfaces.ChatItemClickListener;
import com.yohbmeetup.yohb.models.LogCall;
import com.yohbmeetup.yohb.models.User;
import com.yohbmeetup.yohb.utils.Helper;

import java.util.ArrayList;
import java.util.Locale;

public class LogCallAdapter extends RecyclerView.Adapter<LogCallAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LogCall> dataList;
    private ChatItemClickListener itemClickListener;

    public LogCallAdapter(Context context, ArrayList<LogCall> dataList) {
        this.context = context;
        this.dataList = dataList;

        if (context instanceof ChatItemClickListener) {
            this.itemClickListener = (ChatItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ChatItemClickListener");
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item_log_call, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView time, duration, userName;

        MyViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            time = itemView.findViewById(R.id.time);
            duration = itemView.findViewById(R.id.duration);
            userName = itemView.findViewById(R.id.userName);

            userImage.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != -1) {
                    itemClickListener.onChatItemClick(dataList.get(pos).getUserId(), dataList.get(pos).getUserName(), pos, userImage);
                }
            });

        }

        public void setData(LogCall logCall) {
            Glide.with(context).load(logCall.getUserImage()).apply(new RequestOptions().placeholder(R.drawable.avatar)).into(userImage);
            userName.setText(logCall.getUserName());
            time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
            time.setCompoundDrawablesWithIntrinsicBounds(logCall.isVideo() ? R.drawable.ic_videocam_dark_gray_24dp : R.drawable.ic_phone_dark_gray_24dp, 0, logCall.getStatus().equals("CANCELED") ? R.drawable.ic_call_missed_24dp : logCall.getStatus().equals("DENIED") || logCall.getStatus().equals("IN") ? R.drawable.ic_call_received_24dp : logCall.getStatus().equals("OUT") ? R.drawable.ic_call_made_24dp : 0, 0);
            //duration.setText(formatTimespan(logCall.getTimeDuration()));
        }

        private String formatTimespan(int totalSeconds) {
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }
}
