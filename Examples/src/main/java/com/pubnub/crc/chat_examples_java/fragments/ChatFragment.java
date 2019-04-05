package com.pubnub.crc.chat_examples_java.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.crc.chat_examples_java.R;
import com.pubnub.crc.chat_examples_java.adapters.ChatAdapter;
import com.pubnub.crc.chat_examples_java.prefs.Prefs;
import com.pubnub.crc.chat_examples_java.pubnub.Message;
import com.pubnub.crc.chat_examples_java.view.MessageComposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class ChatFragment extends ParentFragment implements MessageComposer.Listener {

    private static final String ARGS_CHANNEL = "ARGS_CHANNEL";

    @BindView(R.id.chat_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.chat_recyclerview)
    RecyclerView mChatsRecyclerView;

    @BindView(R.id.chats_message_composer)
    MessageComposer mMessageComposer;

    ChatAdapter mChatAdapter;
    List<Message> mMessages = new ArrayList<>();

    private String mChannel;
    private SubscribeCallback mPubNubListener;

    public static ChatFragment newInstance(String channel) {
        Bundle args = new Bundle();
        args.putString(ARGS_CHANNEL, channel);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void setViewBehaviour() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentContext);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fragmentContext, RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.chats_divider));
        mChatsRecyclerView.addItemDecoration(dividerItemDecoration);

        mChatsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChatAdapter = new ChatAdapter(mChannel, mMessages);
        mChatsRecyclerView.setAdapter(mChatAdapter);

        mMessageComposer.setListener(this);
    }

    @Override
    public String setScreenTitle() {
        return mChannel;
    }

    @Override
    public void extractArguments() {
        super.extractArguments();
        mChannel = getArguments().getString(ARGS_CHANNEL);
    }

    @Override
    public void setupData(@Nullable Bundle savedInstanceState) {
        addPubNubListener();
        subscribe();
    }

    private void addPubNubListener() {
        mPubNubListener = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                handleNewMessage(message);

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        };
        hostActivity.getPubNub().addListener(mPubNubListener);
    }

    private void handleNewMessage(PNMessageResult message) {
        if (message.getChannel().equals(mChannel)) {
            Message msg = Message.newBuilder()
                    .senderId(message.getMessage().getAsJsonObject().get("senderId").getAsString())
                    .text(message.getMessage().getAsJsonObject().get("text").getAsString())
                    .timetoken(message.getTimetoken() / 10_000L)
                    .build();
            mMessages.add(msg);
            runOnUiThread(() -> {
                mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount());
                scrollChatToBottom();
            });
        }
    }

    private void subscribe() {
        hostActivity.getPubNub()
                .subscribe()
                .channels(Collections.singletonList(mChannel))
                .withPresence()
                .execute();
    }

    @Override
    public void onDestroy() {
        hostActivity.getPubNub().removeListener(mPubNubListener);
        super.onDestroy();
    }

    @Override
    public void onSentClick(String message) {
        if (TextUtils.isEmpty(message))
            message = UUID.randomUUID().toString();
        hostActivity.getPubNub()
                .publish()
                .channel(mChannel)
                .message(Message.newBuilder().senderId(Prefs.get().uuid()).text(message).build())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (!status.isError()) {

                        }
                    }
                });
    }

    private void scrollChatToBottom() {
        mChatsRecyclerView.scrollToPosition(mMessages.size() - 1);
    }
}
