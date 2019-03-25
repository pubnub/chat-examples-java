package com.pubnub.crc.chat_snippets_java;

import com.pubnub.api.PNConfiguration;

public class TestHarness {

    protected PNConfiguration getPnConfiguration() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("demo-36");
        pnConfiguration.setPublishKey("demo-36");
        return pnConfiguration;
    }

}
