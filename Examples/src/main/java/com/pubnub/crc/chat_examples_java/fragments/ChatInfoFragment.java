package com.pubnub.crc.chat_examples_java.fragments;

import android.os.Bundle;
import android.widget.TextView;

import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.crc.chat_examples_java.R;
import com.pubnub.crc.chat_examples_java.adapters.UserAdapter;
import com.pubnub.crc.chat_examples_java.model.Users;
import com.pubnub.crc.chat_examples_java.pubnub.User;

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
    }

    @Override
    public String setScreenTitle() {
        return fragmentContext.getResources().getString(R.string.group_info);
    }

    @Override
    public void onReady() {
        mChannelName.setText(mChannel);
        mDescription.setText(R.string.lorem_ipsum_long);
        fetchAvailableUsers();
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
                                        .status("Available")
                                        .build());
                            }
                            mUserAdapter.notifyDataSetChanged();
                            /*mUserAdapter.notifyItemRangeChanged(0, mUsers.size());*/

                        }
                    }
                });
    }
}
