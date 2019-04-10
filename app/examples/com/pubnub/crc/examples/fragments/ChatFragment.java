package com.pubnub.crc.examples.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.crc.examples.R;
import com.pubnub.crc.examples.adapters.ChatAdapter;
import com.pubnub.crc.examples.prefs.Prefs;
import com.pubnub.crc.examples.pubnub.Message;
import com.pubnub.crc.examples.view.MessageComposer;
import com.pubnub.crc.examples.view.ProgressView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    @BindView(R.id.chat_recycler_view)
    RecyclerView mChatsRecyclerView;

    @BindView(R.id.chats_message_composer)
    MessageComposer mMessageComposer;

    @BindView(R.id.chat_progress_view)
    ProgressView mProgressView;

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
        setHasOptionsMenu(true);

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

        subscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_info:
                hostActivity.addFragment(ChatInfoFragment.newInstance(mChannel));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onReady() {
        initListener();
    }

    private void initListener() {
        mPubNubListener = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                mProgressView.setEnabled(false);
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                handleNewMessage(message);

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        };
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

    @Override
    public SubscribeCallback provideListener() {
        return mPubNubListener;
    }
}
