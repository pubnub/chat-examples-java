package com.pubnub.crc.snippets;

import com.pubnub.api.PNConfiguration;
import com.pubnub.crc.sample.BuildConfig;

class TestHarness {

    protected static final String SUB_KEY = BuildConfig.SUB_KEY;
    protected static final String PUB_KEY = BuildConfig.PUB_KEY;

    PNConfiguration getPnConfiguration() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        return pnConfiguration;
    }

}
