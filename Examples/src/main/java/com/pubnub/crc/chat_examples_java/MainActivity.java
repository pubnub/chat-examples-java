package com.pubnub.crc.chat_examples_java;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.crc.chat_examples_java.fragments.ChatFragment;
import com.pubnub.crc.chat_examples_java.prefs.Prefs;
import com.pubnub.crc.chat_examples_java.pubnub.PubNubListener;
import com.pubnub.crc.chat_examples_java.util.Keys;
import com.pubnub.crc.chat_examples_java.util.ParentActivityImpl;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class MainActivity extends ParentActivity implements ParentActivityImpl {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.container)
    FrameLayout mFragmentContainer;

    private PubNub mPubNub;
    private PubNubListener mPubNubListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePubNub();
        addFragment(ChatFragment.newInstance("demo-animal-chat"));
    }

    @Override
    protected int provideLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void initializePubNub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(Keys.PUB_KEY);
        pnConfiguration.setSubscribeKey(Keys.SUB_KEY);
        pnConfiguration.setUuid(Prefs.get().uuid());
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);
        mPubNub = new PubNub(pnConfiguration);
    }

    @Override
    public PubNub getPubNub() {
        return mPubNub;
    }

    @Override
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if ((getSupportFragmentManager().getBackStackEntryCount() <= 1)) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
