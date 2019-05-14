package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
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
import chatresourcecenter.util.TestHarness;

import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class MessageIntegrationTest extends TestHarness {

    @Test
    public void testSubscribeChannel() {
        final String channel = UUID.randomUUID().toString();
        final AtomicBoolean success = new AtomicBoolean(false);

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

    @Test
    public void testPublishMessages() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        final String messageText = "publish_message";
        final String channel = UUID.randomUUID().toString();

        subscribeToChannel(channel);

        pubNub.publish()
                .message(generateMessage(pubNub, messageText))
                .channel(channel)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        Assert.assertFalse(status.isError());
                        signal.countDown();
                    }
                });

        signal.await();
    }

    @Test
    public void testReceiveMessage() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        final String channel = UUID.randomUUID().toString();
        final String messageText = "receive_message";

        subscribeToChannel(channel);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                Assert.assertTrue(message.getMessage().toString().contains(messageText));
                Assert.assertTrue(message.getMessage().toString().contains(pubNub.getConfiguration().getUuid()));
                signal.countDown();
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        publishMessage(channel, messageText);

        signal.await();
    }

    @Test
    public void testHistoryMessages() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        final String messageText = "history_message";
        final String channel = UUID.randomUUID().toString();

        subscribeToChannel(channel);
        publishMessage(channel, messageText);
        wait(1);

        pubNub.history()
                .channel(channel)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        String message = result.getMessages().get(0).getEntry().toString();
                        Assert.assertFalse(status.isError());
                        Assert.assertTrue(message.contains(pubNub.getConfiguration().getUuid()));
                        Assert.assertTrue(message.contains(messageText));
                        signal.countDown();
                    }
                });

        signal.await();
    }

    @Test
    public void testHistoryChunkMessages() throws InterruptedException {
        final AtomicInteger resursiveHistoryCount = new AtomicInteger(0);

        final String channel = UUID.randomUUID().toString();
        final int messageCount = 120;
        final int expectedHistoryCallCount = 2;

        subscribeToChannel(channel);
        publishMessages(channel, messageCount);
        wait(1);

        getAllMessages(channel, null, resursiveHistoryCount);

        Awaitility.await()
                .atMost(TIMEOUT_LONG, TimeUnit.SECONDS)
                .untilAtomic(resursiveHistoryCount, equalTo(expectedHistoryCallCount));
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
