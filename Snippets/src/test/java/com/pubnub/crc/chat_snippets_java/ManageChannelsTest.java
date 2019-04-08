package com.pubnub.crc.chat_snippets_java;

import com.pubnub.api.PubNub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class ManageChannelsTest extends TestHarness {

    private PubNub pubNub;

    @Before
    public void beforeEach() {
        pubNub = new PubNub(getPnConfiguration());
    }

    @After
    public void afterEach() {
        pubNub.unsubscribeAll();
        pubNub.forceDestroy();
        pubNub = null;
    }

    @Test
    public void testJoiningSingleChannel() {
        // tag::CHAN-1[]
        pubNub.subscribe()
                .channels(Collections.singletonList("room-1"))
                .execute();
        // end::CHAN-1[]
    }

    @Test
    public void testJoiningMultipleChannels() {
        // tag::CHAN-2[]
        pubNub.subscribe()
                .channels(Arrays.asList("room-1", "room-2", "room-3"))
                .execute();
        // end::CHAN-2[]
    }

}
