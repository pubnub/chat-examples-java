package com.pubnub.crc.chat_snippets_java;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConnectToPubNubTest extends TestHarness {

    private PubNub pubnub;

    @Before
    public void beforeEach() {
        pubnub = new PubNub(getPnConfiguration());
    }

    @After
    public void afterEach() {
        pubnub.forceDestroy();
        pubnub = null;
    }

    @Test
    public void testUuid() {
        String uuid = UUID.randomUUID().toString();
        pubnub.getConfiguration().setUuid(uuid);
        assertEquals(uuid, pubnub.getConfiguration().getUuid());
    }

    @Test
    public void testSubscribe() throws PubNubException, InterruptedException {

        pubnub.subscribe()
                .channels(Collections.singletonList("my_channel"))
                .execute();

        Thread.sleep(TimeUnit.SECONDS.toMillis(2));

        PNHereNowResult response = pubnub.hereNow()
                .channels(Collections.singletonList("my_channel"))
                .includeUUIDs(true)
                .sync();

        assertNotNull(response);

        boolean present = false;
        PNHereNowChannelData hereNowChannelData = response.getChannels().get("my_channel");

        assertNotNull(hereNowChannelData.getOccupants());

        for (PNHereNowOccupantData occupant : hereNowChannelData.getOccupants()) {
            if (occupant.getUuid().contains(pubnub.getConfiguration().getUuid())) {
                present = true;
                break;
            }
        }

        assertTrue(present);
    }

    @Test
    public void testUserMetadata() throws PubNubException {
        JsonObject metadata = new JsonObject();
        metadata.addProperty("color", "red");

        PNSetStateResult response = pubnub.setPresenceState()
                .channels(Collections.singletonList(UUID.randomUUID().toString()))
                .state(metadata)
                .sync();

        assertNotNull(response);
        assertEquals(metadata, response.getState().getAsJsonObject());
    }

    @Test
    public void testPublish() throws PubNubException {
        JsonObject message = new JsonObject();
        message.addProperty("text", UUID.randomUUID().toString().substring(0, 5));

        PNPublishResult response = pubnub.publish()
                .channel("my_channel")
                .message(message)
                .sync();

        assertNotNull(response);
    }

}
