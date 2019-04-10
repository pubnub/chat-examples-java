package com.pubnub.crc.examples.fragments;

import android.os.Bundle;
import android.widget.TextView;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.crc.examples.R;
import com.pubnub.crc.examples.adapters.UserAdapter;
import com.pubnub.crc.examples.model.Users;
import com.pubnub.crc.examples.pubnub.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ChatInfoFragment extends ParentFragment {

    private static final String ARGS_CHANNEL = "ARGS_CHANNEL";

    @BindView(R.id.info_channel)
    TextView mChannelName;

    @BindView(R.id.info_description)
    TextView mDescription;

    @BindView(R.id.info_recycler_view)
    RecyclerView mUsersRecyclerView;

    UserAdapter mUserAdapter;
    List<User> mUsers = new ArrayList<>();

    private String mChannel;
    private SubscribeCallback mPubNubListener;

    static ChatInfoFragment newInstance(String channel) {
        Bundle args = new Bundle();
        args.putString(ARGS_CHANNEL, channel);
        ChatInfoFragment fragment = new ChatInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int provideLayoutResourceId() {
        return R.layout.fragment_chat_info;
    }

    @Override
    public void setViewBehaviour() {
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext));
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUserAdapter = new UserAdapter(mChannel, mUsers);
        mUsersRecyclerView.setAdapter(mUserAdapter);
        mChannelName.setText(mChannel);
        mDescription.setText(R.string.lorem_ipsum_long);

        fetchAvailableUsers();
    }

    @Override
    public String setScreenTitle() {
        return fragmentContext.getResources().getString(R.string.group_info);
    }

    @Override
    public void onReady() {
        initListener();
    }

    private void initListener() {
        mPubNubListener = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                if (presence.getUuid() == null) {
                    return;
                }

                switch (presence.getEvent()) {
                    case "join":
                        mUsers.add(User.newBuilder().user(Users.getUserById(presence.getUuid())).build());
                        break;
                    case "leave":
                    case "timeout":
                        mUsers.remove(User.newBuilder().user(Users.getUserById(presence.getUuid())).build());
                        break;
                    case "interval":
                        for (String uuid : presence.getJoin()) {
                            mUsers.add(User.newBuilder().user(Users.getUserById(uuid)).build());
                        }
                        for (String uuid : presence.getLeave()) {
                            mUsers.remove(User.newBuilder().user(Users.getUserById(uuid)).build());
                        }
                        for (String uuid : presence.getTimeout()) {
                            mUsers.remove(User.newBuilder().user(Users.getUserById(uuid)).build());
                        }
                        break;
                    case "state-change":
                        break;
                }
                runOnUiThread(() -> mUserAdapter.notifyDataSetChanged());
            }
        };
    }

    @Override
    public void extractArguments() {
        super.extractArguments();
        mChannel = getArguments().getString(ARGS_CHANNEL);
    }

    private void fetchAvailableUsers() {
        hostActivity.getPubNub()
                .hereNow()
                .channels(Collections.singletonList(mChannel))
                .includeUUIDs(true)
                .includeState(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (!status.isError()) {
                            mUsers.clear();
                            PNHereNowChannelData hereNowChannelData = result.getChannels().get(mChannel);
                            for (PNHereNowOccupantData occupant : hereNowChannelData.getOccupants()) {
                                mUsers.add(User.newBuilder()
                                        .user(Users.getUserById(occupant.getUuid()))
                                        .build());
                            }
                            mUserAdapter.notifyDataSetChanged();
                            /*mUserAdapter.notifyItemRangeChanged(0, mUsers.size());*/

                        }
                    }
                });
    }

    @Override
    public SubscribeCallback provideListener() {
        return mPubNubListener;
    }
}
