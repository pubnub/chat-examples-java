package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.Arrays;
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

        String uuid = randomUuid();
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
        final String expectedChannel = randomUuid();

        // tag::CON-4[]
        JsonObject state = new JsonObject();
        state.addProperty("mood", "grumpy");

        pubNub.setPresenceState()
                .state(state)
                // tag::ignore[]
                .channels(Arrays.asList(expectedChannel))
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("room-1"))
                // tag::ignore[]
                */
                // end::ignore[]
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
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(setStateSuccess);

        final AtomicBoolean getStateSuccess = new AtomicBoolean(false);
        // tag::CON-5[]
        pubNub.getPresenceState()
                // tag::ignore[]
                .channels(Arrays.asList(expectedChannel))
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("room-1"))
                // tag::ignore[]
                */
                // end::ignore[]
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        // tag::ignore[]
                        assertNotNull(status);
                        assertNotNull(result);
                        assertFalse(status.isError());
                        assertEquals(result.getStateByUUID()
                                .get(expectedChannel)
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

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(getStateSuccess);
    }

    @Test
    public void testDisconnecting() {
        final AtomicBoolean unsubscribedSuccess = new AtomicBoolean(false);
        final String expectedChannel = randomUuid();

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    pubNub.subscribe()
                            // tag::ignore[]
                            .channels(Arrays.asList(expectedChannel))
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channels(Arrays.asList("room-1"))
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .withPresence()
                            .execute();
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("leave") && presence.getUuid()
                        .equals(pubNub.getConfiguration().getUuid())) {
                    unsubscribedSuccess.set(true);
                }
            }
        });

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    // tag::CON-6[]
                    pubNub.unsubscribeAll();
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
                // tag::ignore[]
                .channels(Arrays.asList(expectedChannel))
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("room-1"))
                // tag::ignore[]
                */
                // end::ignore[]
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(unsubscribedSuccess);
    }

    @Test
    public void testReconnectingManually() {
        // tag::CON-7.1[]
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);

        PubNub pubNub = new PubNub(pnConfiguration);
        // end::CON-7.1[]

        // tag::CON-7.2[]
        /*
         * If connection availability check will be done in other way,
         * then use this  function to reconnect to PubNub.
         */
        pubNub.reconnect();
        // end::CON-7.2[]

        assertNotNull(pubNub);
        assertEquals(pubNub.getConfiguration().getReconnectionPolicy(), PNReconnectionPolicy.LINEAR);
    }

}

