package resourcecenterdemo.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import resourcecenterdemo.R;
import resourcecenterdemo.adapters.UserAdapter;
import resourcecenterdemo.model.Users;
import resourcecenterdemo.pubnub.User;
import resourcecenterdemo.util.GlideApp;
import resourcecenterdemo.view.EmptyView;

public class ChatInfoFragment extends ParentFragment {

    private static final String ARGS_CHANNEL = "ARGS_CHANNEL";

    @BindView(R.id.info_image)
    ImageView mImage;

    @BindView(R.id.info_description)
    TextView mDescription;

    @BindView(R.id.info_recycler_view)
    RecyclerView mUsersRecyclerView;

    @BindView(R.id.info_empty_view)
    EmptyView mEmptyView;

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
    public void setViewBehaviour(boolean viewFromCache) {
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentContext));
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUserAdapter = new UserAdapter(mChannel);
        mUsersRecyclerView.setAdapter(mUserAdapter);

        GlideApp.with(fragmentContext)
                .load(R.drawable.chat_logo)
                .apply(RequestOptions.circleCropTransform())
                .into(mImage);

        fetchAvailableUsers();
    }

    @Override
    public String setScreenTitle() {
        hostActivity.enableBackButton(true);
        return mChannel;
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

                handleUiVisibility();

                runOnUiThread(() -> mUserAdapter.update(mUsers));
            }
        };
    }

    private void handleUiVisibility() {
        int viewState = -1;

        if (mUsers.size() > 0) {
            if (mEmptyView.getVisibility() != View.GONE)
                viewState = View.GONE;
        } else {
            if (mEmptyView.getVisibility() != View.VISIBLE)
                viewState = View.VISIBLE;
        }

        if (viewState != -1) {
            int finalViewState = viewState;
            runOnUiThread(() -> mEmptyView.setVisibility(finalViewState));
        }
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
                            mUserAdapter.update(mUsers);

                        }
                    }
                });
    }

    @Override
    public SubscribeCallback provideListener() {
        return mPubNubListener;
    }

}
