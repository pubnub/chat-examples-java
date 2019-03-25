package com.pubnub.crc.chat_snippets_java;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ConnectToPubNubTest extends TestHarness {

    private PubNub pubnub;

    @Before
    public void beforeEach() {
        pubnub = new PubNub(getPnConfiguration());
    }

    @After
    public void afterEach() {
        pubnub.destroy();
        pubnub = null;
    }

    @Test
    public void testUuid() {
        String uuid = UUID.randomUUID().toString();
        pubnub.getConfiguration().setUuid(uuid);
        assertEquals(uuid, pubnub.getConfiguration().getUuid());
    }

    @Test
    public void testUserMetadata() {
        final JsonObject metadata = new JsonObject();
        metadata.addProperty("color", "red");

        pubnub.setPresenceState()
                .channels(Collections.singletonList(UUID.randomUUID().toString()))
                .state(metadata)
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(PNSetStateResult result, PNStatus status) {
                        assertNotNull(result);
                        assertFalse(status.isError());
                        assertEquals(metadata, result.getState().getAsJsonObject());
                    }
                });
    }

}
