package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class MessageIntegrationTest extends TestHarness {

    @Test
    public void testSubscribeChannel() {
        final AtomicBoolean success = new AtomicBoolean(false);

        final String channel = randomUuid();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                Assert.assertNotNull(presence.getUuid());
                if (presence.getEvent().equals("join")) {
                    Assert.assertEquals(channel, presence.getChannel());
                    Assert.assertEquals(pubNub.getConfiguration().getUuid(), presence.getUuid());
                    success.set(true);
                }
            }
        });

        subscribeToChannel(channel);

        Awaitility.await().atMost(TIMEOUT_SHORT, TimeUnit.SECONDS).untilTrue(success);
    }

    // tag::TEST-1[]
    @Test
    public void testPublishMessages() {
        final AtomicBoolean success = new AtomicBoolean(false);

        final String message = randomUuid();
        final String channel = randomUuid();

        subscribeToChannel(channel);

        pubNub.publish()
                .message(message)
                .channel(channel)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        Assert.assertFalse(status.isError());
                        Assert.assertNotNull(result.getTimetoken());
                        success.set(true);
                    }
                });

        Awaitility.await().atMost(TIMEOUT_SHORT, TimeUnit.SECONDS).untilTrue(success);
    }
    // end::TEST-1[]

    @Test
    public void testReceiveMessage() {
        final AtomicBoolean success = new AtomicBoolean(false);

        final String channel = randomUuid();

        JsonObject messagePayload = randomMessage();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() == PNOperationType.PNSubscribeOperation
                        && status.getAffectedChannels().contains(channel)) {
                    publishMessage(channel, messagePayload);
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                Assert.assertEquals(messagePayload, message.getMessage());
                success.set(true);
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        subscribeToChannel(channel);

        Awaitility.await().atMost(TIMEOUT_SHORT, TimeUnit.SECONDS).untilTrue(success);
    }

    @Test
    public void testChannelHistory() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        final JsonObject messagePayload = randomMessage();
        final String channel = randomUuid();

        publishMessage(channel, messagePayload);

        wait(1);

        pubNub.history()
                .channel(channel)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        Assert.assertFalse(status.isError());
                        Assert.assertEquals(messagePayload, result.getMessages().get(0).getEntry());
                        signal.countDown();
                    }
                });

        signal.await();
    }

    @Test
    public void testHistoryChunkMessages() {
        final AtomicInteger recursiveHistoryCount = new AtomicInteger(0);

        final String channel = UUID.randomUUID().toString();
        final int messageCount = 120;
        final int expectedHistoryCallCount = 2;

        subscribeToChannel(channel);
        publishMessages(channel, messageCount);

        wait(TIMEOUT_MEDIUM);

        getAllMessages(channel, null, recursiveHistoryCount);

        Awaitility.await().atMost(TIMEOUT_LONG, TimeUnit.SECONDS)
                .untilAtomic(recursiveHistoryCount, equalTo(expectedHistoryCallCount));
    }

    private void getAllMessages(String channel, Long startTimeToken, AtomicInteger historyCallCount) {
        pubNub.history()
                .channel(channel)
                .start(startTimeToken)
                .reverse(false)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        Assert.assertFalse(status.isError());
                        Assert.assertNotNull(result);

                        List<PNHistoryItemResult> messages = result.getMessages();
                        if (messages.size() == 100) {
                            getAllMessages(channel, result.getStartTimetoken(), historyCallCount);
                        }
                        historyCallCount.incrementAndGet();
                    }
                });
    }

}
