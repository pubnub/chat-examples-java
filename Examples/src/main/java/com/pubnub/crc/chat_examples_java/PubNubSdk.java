package com.pubnub.crc.chat_examples_java;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class PubNubSdk {

    private static PubNubSdk sInstance;
    private static final String TAG = "PubNubSdk";

    private PubNub mPubNub;

    private PubNubSdk(PNConfiguration pnConfiguration) {
        mPubNub = new PubNub(pnConfiguration);
    }

    public static void initialize(PNConfiguration pnConfiguration) {
        if (pnConfiguration == null) {
            throw new IllegalStateException("PNConfiguration must not be null");
        }
        if (sInstance == null) {
            sInstance = new PubNubSdk(pnConfiguration);
        }
    }

    public static PubNubSdk get() {
        if (sInstance == null) {
            throw new IllegalStateException("PubNubSdk is not initialized. call initialize method first.");
        }
        return sInstance;
    }



}
