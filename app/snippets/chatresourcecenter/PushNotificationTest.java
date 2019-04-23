package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.api.models.consumer.push.PNPushRemoveChannelResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class PushNotificationTest extends TestHarness {

    @Test
    public void testAddDeviceToken() {
        String firebaseInstanceId = UUID.randomUUID().toString();
        // tag::PUSH-1[]
        pubNub.addPushNotificationsOnChannels()
                .channels(Arrays.asList("ch1"))
                .pushType(PNPushType.GCM)
                .deviceId(firebaseInstanceId)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {

                    }
                });
        // end::PUSH-1[]
    }

    @Test
    public void testRemoveDeviceToken() {
        String firebaseInstanceId = UUID.randomUUID().toString();
        // tag::PUSH-2[]
        pubNub.removePushNotificationsFromChannels()
                .channels(Arrays.asList("ch1"))
                .pushType(PNPushType.GCM)
                .deviceId(firebaseInstanceId)
                .async(new PNCallback<PNPushRemoveChannelResult>() {
                    @Override
                    public void onResponse(PNPushRemoveChannelResult result, PNStatus status) {
                        // handle status, response
                    }
                });
        // end::PUSH-2[]
    }

    @Test
    public void testFormattingMessages() {
        final AtomicBoolean formattedMessageSentSuccess = new AtomicBoolean(false);
        final String expectedChannel = UUID.randomUUID().toString();

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    // tag::PUSH-3[]
                    JsonObject payload = new JsonObject();

                    JsonObject message = new JsonObject();
                    JsonObject gcmData = new JsonObject();

                    message.addProperty("league", "NBA");
                    message.addProperty("match", "Orlando Magic - Toronto Raptors");
                    message.addProperty("date", "22. Apr 2019, 01:00");

                    gcmData.add("data", message);

                    payload.add("message", message);
                    payload.add("pn_gcm", gcmData);

                    pubNub.publish()
                            // tag::ignore[]
                            .channel(expectedChannel)
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channel("ch1")
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .message(payload)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // tag::ignore[]
                                    assertFalse(status.isError());
                                    assertNotNull(result);
                                    formattedMessageSentSuccess.set(true);
                                    // end::ignore[]
                                }
                            });
                    // end::PUSH-3[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                if (message.getPublisher().equals(getUuid()) && message.getChannel().equals(expectedChannel)) {
                    formattedMessageSentSuccess.set(true);
                } else {
                    formattedMessageSentSuccess.set(false);
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        observerClient.subscribe()
                .channels(Arrays.asList(expectedChannel))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(formattedMessageSentSuccess);
    }

}
