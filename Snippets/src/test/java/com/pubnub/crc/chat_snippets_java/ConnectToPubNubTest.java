package com.pubnub.crc.chat_snippets_java;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ConnectToPubNubTest extends TestHarness {

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
    public void testSetup() {
        /*
        // tag::CON-1[]
        implementation 'com.pubnub:pubnub-gson:4.22.0-beta'
        // end::CON-1[]
        */
    }

    @Test
    public void testInitializingPubNub() {
        // tag::CON-2[]
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);

        PubNub pubNub = new PubNub(pnConfiguration);
        // end::CON-2[]

        assertNotNull(pubNub);
        assertNotNull(pubNub.getConfiguration().getUuid());
    }

    @Test
    public void testSettingUuid() {
        // tag::CON-3[]
        String uuid = UUID.randomUUID().toString();

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        pnConfiguration.setUuid(uuid);

        PubNub pubNub = new PubNub(pnConfiguration);
        // end::CON-3[]
        assertNotNull(uuid);
        assertNotNull(pubNub);
        assertEquals(uuid, pubNub.getConfiguration().getUuid());
    }

    @Test
    public void testSettingState() {
        final AtomicBoolean setStateSuccess = new AtomicBoolean(false);
        // tag::CON-4[]
        JsonObject state = new JsonObject();
        state.addProperty("mood", "grumpy");

        pubNub.setPresenceState()
                .state(state)
                .channels(Collections.singletonList("room-1"))
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(PNSetStateResult result, PNStatus status) {
                        // tag::ignore[]
                        assertNotNull(status);
                        assertNotNull(result);
                        assertFalse(status.isError());
                        assertEquals(result.getState(), state);
                        setStateSuccess.set(true);
                        // end::ignore[]
                        if (!status.isError()) {
                            // handle state setting response
                        }
                    }
                });
        // end::CON-4[]
        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilTrue(setStateSuccess);

        final AtomicBoolean getStateSuccess = new AtomicBoolean(false);
        // tag::CON-5[]
        pubNub.getPresenceState()
                .channels(Collections.singletonList("room-1"))
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        // tag::ignore[]
                        assertNotNull(status);
                        assertNotNull(result);
                        assertFalse(status.isError());
                        assertEquals(result.getStateByUUID()
                                .get("room-1")
                                .getAsJsonObject()
                                .get("mood"), state.get("mood"));
                        getStateSuccess.set(true);
                        // end::ignore[]
                        if (!status.isError()) {
                            // handle state setting response
                        }
                    }
                });
        // end::CON-5[]

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilTrue(getStateSuccess);
    }

    @Test
    public void testDisconnecting() {
        // tag::CON-6[]
        pubNub.unsubscribeAll();
        // end::CON-6[]
    }

    @Test
    public void testReconnectingManually() {
        // tag::CON-7[]
        pubNub.reconnect();
        // end::CON-7[]
    }

}

