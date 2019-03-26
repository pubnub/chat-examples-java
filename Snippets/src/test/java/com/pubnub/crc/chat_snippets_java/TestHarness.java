package com.pubnub.crc.chat_snippets_java;

import com.pubnub.api.PNConfiguration;

class TestHarness {

    static final String SUB_KEY = "demo-36";
    static final String PUB_KEY = "demo-36";

    PNConfiguration getPnConfiguration() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        return pnConfiguration;
    }

}
