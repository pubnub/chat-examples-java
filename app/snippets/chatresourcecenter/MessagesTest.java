package chatresourcecenter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.PubNubUtil;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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

    // tag::MSG-3[]
    // Calculating a PubNub Message Payload Size
    private int payloadSize(String channel, Object message) throws PubNubException {
        String encodedPayload = PubNubUtil.urlEncode(channel + "/" + pubNub.getMapper().toJson(message));
        System.out.println(encodedPayload);
        return encodedPayload.length() + 150; // 150 is length of publish API prefix.
    }
    // end::MSG-3[]

    @Test
    public void testSendImagesAndFiles() throws PubNubException {
        // tag::MSG-3[]
        // usage example
        final String channel = "room-1";

        JsonObject messagePayload = new JsonObject();
        messagePayload.addProperty("senderId", "user123");
        messagePayload.addProperty("text", "Hello World");

        int size = payloadSize(channel, messagePayload);

        Log.i("payload_size", String.valueOf(size));
        // end::MSG-3[]
        assertEquals(230, size);
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
    public void testUpdatingMessages() {
        final AtomicBoolean messageUpdatedSuccess = new AtomicBoolean(false);

        final String expectedChannel = randomUuid();
        final String expectedText = randomUuid();
        final AtomicLong initialTimetoken = new AtomicLong(0);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    // tag::MSG-6.1[]
                    // tag::ignore[]
                    /*
                    // end::ignore[]
                    Long firstMessageTimeToken;
                    // tag::ignore[]
                    */
                    // end::ignore[]

                    JsonObject messagePayload = new JsonObject();
                    messagePayload.addProperty("senderId", "user123");
                    messagePayload.addProperty("text", "Hello, hoomans!");

                    pubNub.publish()
                            // tag::ignore[]
                            .channel(expectedChannel)
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channel("room-1")
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .message(messagePayload)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // tag::ignore[]
                                    assertFalse(status.isError());
                                    assertNotNull(result);
                                    initialTimetoken.set(result.getTimetoken());
                                    // end::ignore[]
                                    // tag::ignore[]
                                    /*
                                    // end::ignore[]
                                    if (!status.isError()) {
                                        firstMessageTimeToken = result.getTimetoken();
                                    }
                                    // tag::ignore[]
                                    */
                                    // end::ignore[]
                                }
                            });
                    // end::MSG-6.1[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                if (message.getChannel().equals(expectedChannel) && message.getPublisher().equals(getUuid())) {
                    if (!message.getMessage().getAsJsonObject().has("timetoken")) {
                        // tag::MSG-6.2[]
                        JsonObject messagePayload = new JsonObject();
                        // tag::ignore[]
                        /*
                        // end::ignore[]
                        messagePayload.addProperty("timetoken", firstMessageTimeToken);
                        // tag::ignore[]
                        */
                        // end::ignore[]
                        // tag::ignore[]
                        messagePayload.addProperty("timetoken", message.getTimetoken());
                        // end::ignore[]
                        messagePayload.addProperty("senderId", "user123");
                        // tag::ignore[]
                        messagePayload.addProperty("text", expectedText);
                        // end::ignore[]
                        // tag::ignore[]
                        /*
                        // end::ignore[]
                        messagePayload.addProperty("text", "Fixed. I had a typo earlier...");
                        // tag::ignore[]
                        */
                        // end::ignore[]

                        pubNub.publish()
                                // tag::ignore[]
                                .channel(expectedChannel)
                                // end::ignore[]
                                // tag::ignore[]
                                /*
                                // end::ignore[]
                                .channel("room-1")
                                // tag::ignore[]
                                */
                                // end::ignore[]
                                .message(messagePayload)
                                .async(new PNCallback<PNPublishResult>() {
                                    @Override
                                    public void onResponse(PNPublishResult result, PNStatus status) {
                                        // tag::ignore[]
                                        assertFalse(status.isError());
                                        assertNotNull(result);
                                        // end::ignore[]
                                        // handle status, response
                                    }
                                });
                        // end::MSG-6.2[]
                    } else {
                        assertEquals(expectedText, message.getMessage().getAsJsonObject().get("text").getAsString());
                        assertEquals(initialTimetoken.get(), message.getMessage()
                                .getAsJsonObject()
                                .get("timetoken")
                                .getAsLong());
                        messageUpdatedSuccess.set(true);
                    }

                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubNub.subscribe()
                .channels(Arrays.asList(expectedChannel))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(messageUpdatedSuccess);
    }

    @Test
    public void testSendingAnnouncements() {
        final AtomicBoolean announcementSentSuccess = new AtomicBoolean(false);

        final String expectedChannel = randomUuid();
        final String expectedText = randomUuid();

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {

                    // tag::MSG-7[]
                    JsonObject messagePayload = new JsonObject();
                    messagePayload.addProperty("senderId", "user123");
                    // tag::ignore[]
                    /*
                    // end::ignore[]
                    messagePayload.addProperty("text", "Hello, this is an announcement");
                    // tag::ignore[]
                    */
                    // end::ignore[]
                    // tag::ignore[]
                    messagePayload.addProperty("text", expectedText);
                    // end::ignore[]

                    pubNub.publish()
                            .message(messagePayload)
                            // tag::ignore[]
                            .channel(expectedChannel)
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channel("room-1")
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    // tag::ignore[]
                                    assertFalse(status.isError());
                                    assertNotNull(result);
                                    // end::ignore[]
                                    // handle status, response
                                }
                            });
                    // end::MSG-7[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                if (message.getChannel().equals(expectedChannel) && message.getPublisher().equals(getUuid())) {
                    assertEquals(expectedText, message.getMessage().getAsJsonObject().get("text").getAsString());
                    announcementSentSuccess.set(true);
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        observerClient.subscribe()
                .channels(Arrays.asList(expectedChannel))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(announcementSentSuccess);
    }

}
