package chatresourcecenter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import chatresourcecenter.mock.Log;

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
                        // end::ignore[]
                        if (!status.isError()) {
                            // message is sent
                            Long timetoken = result.getTimetoken(); // message timetoken
                        }
                    }
                });
        // end::MSG-1[]
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(messageSendSuccess);
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
                // end::ignore[]
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
                // end::ignore[]
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        // end::MSG-2[]

        pubNub.subscribe()
                .channels(Arrays.asList("room-1"))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(messageReceivedSuccess);

    }

    @Test
    public void testSendImagesAndFiles() {
        // tag::MSG-3[]
        // in progress
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
                        // end::ignore[]
                    }
                });
        // end::MSG-4[]
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(typingIndicatorSendSuccess);
    }

    @Test
    public void testShowMessageTimestamp() {
        final AtomicBoolean timestampShownSuccess = new AtomicBoolean(false);
        // tag::MSG-5[]
        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // tag::ignore[]
                if (PnUtils.isSubscribed(status, "room-1")) {
                    JsonObject message = new JsonObject();
                    message.addProperty("senderId", "user123");
                    message.addProperty("text", "hello");
                    pubNub.publish()
                            .channel("room-1")
                            .message(message)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    assertFalse(status.isError());
                                }
                            });
                }
                // end::ignore[]
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // tag::ignore[]
                assertEquals("room-1", message.getChannel());
                assertEquals(getUuid(), message.getPublisher());
                // end::ignore[]
                long timetoken = message.getTimetoken() / 10_000L;
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timetoken);
                String localDateTime = sdf.format(calendar.getTimeInMillis());
                Log.d("localDateTime", localDateTime);
                // tag::ignore[]
                timestampShownSuccess.set(true);
                // end::ignore[]
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        // end::MSG-5[]

        pubNub.subscribe()
                .channels(Arrays.asList("room-1"))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(timestampShownSuccess);
    }

    @Test
    public void testUpdatingMessages() throws PubNubException, InterruptedException {
        // tag::MSG-6.2[]
        // progress
        // end::MSG-6.2[]
        // tag::MSG-6.1[]
        // in
        // end::MSG-6.1[]
        final AtomicBoolean messageUpdatedSuccess = new AtomicBoolean(false);
        final String expectedChannel = UUID.randomUUID().toString();
        // final String expectedChannel = "apr19mo";

        System.out.println("History " + getHistory(expectedChannel).getMessages().size());

        Long initialTimeToken;

        /*{
            JsonObject messagePayload = new JsonObject();
            messagePayload.addProperty("senderId", "user123");
            messagePayload.addProperty("text", "Hello, hoomans!");

            initialTimeToken = publishMessage(messagePayload, expectedChannel);
            System.out.println("initial timetoken publish " + initialTimeToken);

            TimeUnit.SECONDS.sleep(TIMEOUT_SHORT);

            PNHistoryResult history = getHistory(expectedChannel);
            System.out.println("History size: " + history.getMessages().size());
            for (PNHistoryItemResult message : history.getMessages()) {
                System.out.println("initial timetoken history " + message.getTimetoken());
                System.out.println(message.getTimetoken() + ": " + message.getEntry());
            }
        }

        // edit

        {
            JsonObject messagePayload = new JsonObject();
            messagePayload.addProperty("senderId", "user123");
            messagePayload.addProperty("text", "Edit: Hello, hoomans!");
            messagePayload.addProperty("timetoken", initialTimeToken);

            System.out.println("About to publish: " + messagePayload.toString());

            Long secondTimetoken = publishMessage(messagePayload, expectedChannel);

            TimeUnit.SECONDS.sleep(TIMEOUT_SHORT);

            PNHistoryResult history = getHistory(expectedChannel);
            System.out.println("History size: " + history.getMessages().size());
            for (PNHistoryItemResult message : history.getMessages()) {
                System.out.println(message.getTimetoken() + ": " + message.getEntry());
            }
        }*/

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(messageUpdatedSuccess);
    }

    @Test
    public void testSendingAnnouncements() {
        final AtomicBoolean announcementSentSuccess = new AtomicBoolean(false);
        // tag::MSG-7[]
        // in progress
        // end::MSG-7[]
    }

    private PNHistoryResult getHistory(String channel) throws PubNubException {
        return pubNub.history()
                .channel(channel)
                .count(10)
                .includeTimetoken(true)
                .sync();
    }

    private Long publishMessage(JsonObject payload, String channel) throws PubNubException {
        return pubNub.publish()
                .message(payload)
                .channel(channel)
                .shouldStore(true)
                .sync()
                .getTimetoken();
    }

}
