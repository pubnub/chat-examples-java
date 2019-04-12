package chatresourcecenter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
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

public class MessagesTest extends TestHarness {

    @Test
    public void testSendMessage() {
        final AtomicBoolean messageSendSuccess = new AtomicBoolean(false);

        JsonObject message = new JsonObject();
        message.addProperty("senderId", "user123");
        message.addProperty("text", "hello");
        // tag::MSG-1[]
        pubNub.publish()
                .message(message)
                .channel("room-1")
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        assertNotNull(result);
                        messageSendSuccess.set(true);
                        // tag::ignore[]
                        if (!status.isError()) {
                            // message is sent
                            Long timetoken = result.getTimetoken(); // message timetoken
                        }
                    }
                });
        // end::MSG-1[]
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(messageSendSuccess);
    }

    @Test
    public void testReceiveMessage() {
        final AtomicBoolean messageReceivedSuccess = new AtomicBoolean(false);

        JsonObject messageObject = new JsonObject();

        // tag::MSG-2[]
        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // tag::ignore[]
                if (PnUtils.isSubscribed(status, "room-1")) {
                    messageObject.addProperty("senderId", "user123");
                    messageObject.addProperty("text", "hello");
                    pubNub.publish()
                            .channel("room-1")
                            .message(messageObject)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    assertFalse(status.isError());
                                }
                            });
                }
                // tag::ignore[]
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // the channel for which the message belongs
                String channel = message.getChannel();

                // the channel group or wildcard subscription match (if exists)
                String channelGroup = message.getSubscription();

                // publish timetoken
                Long publishTimetoken = message.getTimetoken();

                // the payload
                JsonElement messagePayload = message.getMessage();

                // the publisher
                String publisher = message.getPublisher();

                // tag::ignore[]
                assertEquals("room-1", channel);
                assertEquals(publisher, getUuid());
                assertEquals(messageObject, messagePayload);
                messageReceivedSuccess.set(true);
                // tag::ignore[]
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        // end::MSG-2[]

        pubNub.subscribe()
                .channels(Arrays.asList("room-1"))
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(messageReceivedSuccess);

    }

    @Test
    public void testSendImagesAndFiles() {
        // tag::MSG-3[]
        // end::MSG-3[]
    }

    @Test
    public void testSendTypingIndicators() {
        final AtomicBoolean typingIndicatorSendSuccess = new AtomicBoolean(false);
        // tag::MSG-4[]
        JsonObject message = new JsonObject();
        message.addProperty("senderId", "user123");
        message.addProperty("isTyping", true);

        pubNub.publish()
                .channel("room-1")
                .message(message)
                .shouldStore(false)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        typingIndicatorSendSuccess.set(true);
                        // tag::ignore[]
                    }
                });
        // end::MSG-4[]
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(typingIndicatorSendSuccess);
    }

}
