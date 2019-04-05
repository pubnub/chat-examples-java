package com.pubnub.crc.chat_examples_java.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.pubnub.crc.chat_examples_java.R;
import com.pubnub.crc.chat_examples_java.pubnub.Message;
import com.pubnub.crc.chat_examples_java.util.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final int TYPE_TXT_OWN = 0;
    private final int TYPE_TXT_REC = 1;

    private final String mChannel;

    private List<Message> mItems;

    public ChatAdapter(String channel, List<Message> items) {
        mChannel = channel;
        mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).isOwnMessage())
            return TYPE_TXT_OWN;
        return TYPE_TXT_REC;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TXT_OWN:
                View sentMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageViewHolder(sentMessageView);
            case TYPE_TXT_REC:
                View receivedMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageViewHolder(receivedMessageView);
        }
        throw new IllegalStateException("No applicable viewtype found.");
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_avatar)
        ImageView mAvatar;

        @BindView(R.id.message_sender)
        TextView mSender;

        @BindView(R.id.message_bubble)
        TextView mBubble;

        @BindView(R.id.message_timestamp)
        TextView mTimestamp;

        @OnClick(R.id.root)
        void rootClick(View v) {

        }

        Message mMessage;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(Message message) {
            this.mMessage = message;

            mBubble.setText(mMessage.getText());
            mSender.setText(mMessage.getUser().getDisplayName());
            mTimestamp.setText(mMessage.getTimestamp());

            GlideApp.with(this.itemView)
                    .load(mMessage.getUser().getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
        }
    }
}
