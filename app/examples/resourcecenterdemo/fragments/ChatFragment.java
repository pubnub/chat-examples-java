package resourcecenterdemo.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import resourcecenterdemo.R;
import resourcecenterdemo.adapters.ChatAdapter;
import resourcecenterdemo.prefs.Prefs;
import resourcecenterdemo.pubnub.History;
import resourcecenterdemo.pubnub.Message;
import resourcecenterdemo.view.MessageComposer;

public class ChatFragment extends ParentFragment implements MessageComposer.Listener {

    private static final String ARGS_CHANNEL = "ARGS_CHANNEL";

    @BindView(R.id.chat_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.chat_recycler_view)
    RecyclerView mChatsRecyclerView;

    @BindView(R.id.chats_message_composer)
    MessageComposer mMessageComposer;

    ChatAdapter mChatAdapter;
    List<Message> mMessages = new ArrayList<>();

    private String mChannel;
    private SubscribeCallback mPubNubListener;

    private RecyclerView.OnScrollListener mOnScrollListener;
    private int topItemOffset = 3;
    private int historyChunkSize = 10;

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
    public void setViewBehaviour(boolean viewFromCache) {
        setHasOptionsMenu(true);

        initializeScrollListener();

        prepareRecyclerView();

        if (!viewFromCache) {
            mSwipeRefreshLayout.setRefreshing(true);
            subscribe();
            fetchHistory();
        }
    }

    private void prepareRecyclerView() {

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(this::fetchHistory);

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

        mChatsRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void initializeScrollListener() {
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstCompletelyVisibleItemPosition =
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                if (firstCompletelyVisibleItemPosition == topItemOffset && dy < 0) {
                    fetchHistory();
                }
            }
        };
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
        scrollChatToBottom();
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
                if (status.getOperation() == PNOperationType.PNSubscribeOperation && status.getAffectedChannels()
                        .contains(mChannel)) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
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

    private void fetchHistory() {
        mSwipeRefreshLayout.setRefreshing(true);
        History.getAllMessages(hostActivity.getPubNub(), mChannel, getEarliestTimestamp(), historyChunkSize,
                new History.CallbackSkeleton() {
                    @Override
                    public void handleResponse(List<Message> messages) {
                        runOnUiThread(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (!messages.isEmpty()) {
                                mMessages.addAll(0, messages);
                                mChatAdapter.notifyItemRangeInserted(0, messages.size());
                            } else if (mMessages.isEmpty()) {
                                Toast.makeText(fragmentContext, getString(R.string.channel_empty),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(fragmentContext, getString(R.string.no_more_messages),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                });
    }

    private Long getEarliestTimestamp() {
        if (mMessages != null && !mMessages.isEmpty()) {
            return mMessages.get(0).getTimetoken();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        hostActivity.getPubNub().removeListener(mPubNubListener);
        super.onDestroy();
    }

    @Override
    public void onSentClick(String message) {
        if (TextUtils.isEmpty(message)) {
            message = UUID.randomUUID().toString();
        }
        hostActivity.getPubNub()
                .publish()
                .channel(mChannel)
                .shouldStore(true)
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
