package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
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
import static org.junit.Assert.assertNull;

public class PresenceTest extends TestHarness {

    @Test
    public void testReceivePresenceEvents() {
        final AtomicBoolean presenceEventReceivedSuccess = new AtomicBoolean(false);

        String expectedChannel = UUID.randomUUID().toString();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    observerClient.subscribe()
                            .channels(Collections.singletonList(expectedChannel))
                            .withPresence()
                            .execute();
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("join")
                        && presence.getUuid().equals(observerClient.getConfiguration().getUuid())
                        && presence.getChannel().equals(expectedChannel)) {
                    presenceEventReceivedSuccess.set(true);
                }
            }
        });

        // tag::PRE-1[]
        pubNub.subscribe()
                // tag::ignore[]
                .channels(Collections.singletonList(expectedChannel))
                /*
                // end::ignore[]
                .channels(Arrays.asList("room-1"))
                // tag::ignore[]
                */
                // end::ignore[]
                .withPresence()
                .execute();
        // end::PRE-1[]
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(presenceEventReceivedSuccess);
    }

    @Test
    public void testRequestOnDemandPresenceStatus() {
        final AtomicBoolean presenceStatusReceivedSuccess = new AtomicBoolean(false);

        String expectedChannel = UUID.randomUUID().toString();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    // tag::PRE-2[]
                    pubNub.hereNow()
                            // tag::ignore[]
                            .channels(Collections.singletonList(expectedChannel))
                            /*
                            // end::ignore[]
                            .channels(Arrays.asList("room-1"))
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .includeUUIDs(true)
                            .includeState(true)
                            .async(new PNCallback<PNHereNowResult>() {
                                @Override
                                public void onResponse(PNHereNowResult result, PNStatus status) {
                                    // tag::ignore[]
                                    final int expectedOccupants = 1;
                                    final int expectedIndex = 0;
                                    assertFalse(status.isError());
                                    assertNotNull(result);
                                    assertEquals(expectedOccupants, result.getChannels().size());
                                    assertNotNull(result.getChannels().get(expectedChannel));
                                    assertEquals(expectedChannel, result.getChannels()
                                            .get(expectedChannel)
                                            .getChannelName());
                                    assertEquals(expectedOccupants, result.getChannels()
                                            .get(expectedChannel)
                                            .getOccupants()
                                            .size());
                                    assertEquals(expectedOccupants, result.getChannels()
                                            .get(expectedChannel)
                                            .getOccupancy());
                                    assertEquals(getUuid(), result.getChannels()
                                            .get(expectedChannel)
                                            .getOccupants()
                                            .get(expectedIndex)
                                            .getUuid());
                                    assertNull(result.getChannels()
                                            .get(expectedChannel)
                                            .getOccupants()
                                            .get(expectedIndex)
                                            .getState());
                                    presenceStatusReceivedSuccess.set(true);
                                    // end::ignore[]
                                    // handle status, response
                                }
                            });
                    // end::PRE-2[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubNub.subscribe()
                .channels(Collections.singletonList(expectedChannel))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(presenceStatusReceivedSuccess);
    }

    @Test
    public void testLastOnlineTimestamp() {
        final AtomicBoolean lastOnlineTimestampSuccess = new AtomicBoolean(true);
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(lastOnlineTimestampSuccess);
        // tag::PRE-3[]
        // in progress
        // end::PRE-3[]
    }
}
