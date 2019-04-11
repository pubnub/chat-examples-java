package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ConnectToPubNubTest extends TestHarness {

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

        String uuid = UUID.randomUUID().toString();
        // tag::CON-3[]
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
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(setStateSuccess);

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

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(getStateSuccess);
    }

    @Test
    public void testDisconnecting() {
        final AtomicBoolean unsubscribedSuccess = new AtomicBoolean(false);

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, "room-1")) {
                    pubNub.subscribe()
                            .channels(Collections.singletonList("room-1"))
                            .withPresence()
                            .execute();
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                PnUtils.printPresence(presence);
                if (presence.getEvent().equals("leave") && presence.getUuid()
                        .equals(pubNub.getConfiguration().getUuid())) {
                    unsubscribedSuccess.set(true);
                }
            }
        });

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() == PNOperationType.PNSubscribeOperation) {
                    // tag::CON-6[]
                    pubNub.unsubscribeAll();
                    // tag::ignore[]
                    try {
                        TimeUnit.SECONDS.sleep(TIMEOUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // tag::ignore[]
                    pubNub.destroy();
                    // end::CON-6[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        observerClient.subscribe()
                .channels(Collections.singletonList("room-1"))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(unsubscribedSuccess);
    }

    @Test
    public void testReconnectingManually() {
        // tag::CON-7[]
        pubNub.getConfiguration().setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubNub.reconnect();
        // end::CON-7[]
    }

}

