package chatresourcecenter;

import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import chatresourcecenter.mock.Log;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HistoryTest extends TestHarness {

    @Test
    public void testFetchMessageCount() {
        final AtomicBoolean messageCountSuccess = new AtomicBoolean(false);
        messageCountSuccess.set(true);
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(messageCountSuccess);
        // tag::HIST-1[]
        // in progress
        // end::HIST-1[]
    }

    @Test
    public void testRetrievePastMessages() throws PubNubException, InterruptedException {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);

        final int count = 10;
        String channel = randomUuid();
        publishMessages(channel, count, () -> {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            Long endTimeToken = cal.getTimeInMillis() * 10_000L;

            // tag::HIST-2[]
            pubNub.history()
                    // tag::ignore[]
                    .channel(channel)
                    /*
                    // end::ignore[]
                    .channel("room-1")
                    // tag::ignore[]
                    */
                    // end::ignore[]
                    // tag::ignore[]
                    .end(endTimeToken)
                    // end::ignore[]
                    // tag::ignore[]
                    /*
                    // end::ignore[]
                    .end(13827485876355504L) // timetoken of the last message
                    // tag::ignore[]
                    */
                    // end::ignore[]
                    // tag::ignore[]
                    .count(count)
                    // end::ignore[]
                    // tag::ignore[]
                    /*
                    // end::ignore[]
                    .count(50) // how many items to fetch
                    // tag::ignore[]
                    */
                    // end::ignore[]
                    .reverse(false)
                    .async(new PNCallback<PNHistoryResult>() {
                        @Override
                        public void onResponse(PNHistoryResult result, PNStatus status) {
                            // tag::ignore[]
                            assertFalse(status.isError());
                            assertNotNull(result);
                            assertEquals(count, result.getMessages().size());
                            pastMessagesSuccess.set(true);
                            // end::ignore[]
                            // handle status, response
                        }
                    });
            // end::HIST-2[]
            Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
        });

    }

    @Test
    public void testRetrieveMoreThan100Messages() throws PubNubException, InterruptedException {
        final AtomicInteger resursiveHistoryCount = new AtomicInteger(0);
        final int publishMessageCount = 110;
        final int expectedHistoryCallCount = 2;
        final String channel = randomUuid();

        publishMessages(channel, publishMessageCount, () -> {
            getAllMessages(channel, null, resursiveHistoryCount);
            Awaitility.await()
                    .atMost(TIMEOUT_LONG, TimeUnit.SECONDS)
                    .untilAtomic(resursiveHistoryCount, equalTo(expectedHistoryCallCount));
        });

        // tag::HIST-3.2[]
        // tag::ignore[]
        /*
        // end::ignore[]
        // Usage example:
        getAllMessages(null);
        // tag::ignore[]
        */
        // end::ignore[]
        // end::HIST-3.2[]
    }

    // tag::HIST-3.1[]
    // tag::ignore[]
    private void getAllMessages(String channel, Long startTimeToken, AtomicInteger historyCallCount) {
        // end::ignore[]
        // tag::ignore[]
    /*
    // end::ignore[]
    private void getAllMessages(Long startTimeToken) {
    // tag::ignore[]
    */
        // end::ignore[]
        pubNub.history()
                // tag::ignore[]
                .channel(channel)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channel("room-1")
                // tag::ignore[]
                */
                // end::ignore[]
                .start(startTimeToken)
                .reverse(false)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        assertNotNull(result);
                        // end::ignore[]
                        if (!status.isError()) {
                            List<PNHistoryItemResult> messages = result.getMessages();
                            Long start = result.getStartTimetoken();
                            Long end = result.getEndTimetoken();

                            // if 'messages' were retrieved, do something useful with them
                            if (messages != null && !messages.isEmpty()) {
                                Log.i("messages", String.valueOf(messages.size()));
                                Log.i("messages", "start: " + start);
                                Log.i("messages", "end: " + end);
                            }

                            /*
                             * if 100 'messages' were retrieved, there might be more, call
                             * history again
                             */
                            if (messages.size() == 100) {
                                // tag::ignore[]
                                getAllMessages(channel, start, historyCallCount);
                                // end::ignore[]
                                // tag::ignore[]
                                /*
                                // end::ignore[]
                                getAllMessages(start);
                                // tag::ignore[]
                                */
                                // end::ignore[]
                            }
                            // tag::ignore[]
                            historyCallCount.incrementAndGet();
                            // end::ignore[]
                        }
                    }
                });
    }
    // end::HIST-3.1[]

    @Test
    public void testRetrieveMessagesMultiChannel() throws PubNubException, InterruptedException {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);

        Calendar cal = Calendar.getInstance();
        long start = cal.getTimeInMillis() * 10_000L;
        start--;

        final int channelsCount = 2;
        final int messagesCount = 2;
        List<String> channels = new ArrayList<>(channelsCount);
        for (int i = 0; i < channelsCount; i++) {
            channels.add(randomUuid());
            publishMessages(channels.get(i), messagesCount, () -> {

            });
        }

        long end = Calendar.getInstance().getTimeInMillis() * 10_000L;

        // tag::HIST-4[]
        pubNub.fetchMessages()
                // tag::ignore[]
                .channels(channels)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("ch1", "ch2", "ch3"))
                // tag::ignore[]
                */
                // end::ignore[]
                // tag::ignore[]
                .start(start)
                .end(end)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .start(15343325214676133L)
                .end(15343325004275466L)
                // tag::ignore[]
                */
                // end::ignore[]
                .maximumPerChannel(15)
                .async(new PNCallback<PNFetchMessagesResult>() {
                    @Override
                    public void onResponse(PNFetchMessagesResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        assertNotNull(result);
                        assertEquals(channelsCount, result.getChannels().entrySet().size());
                        for (Map.Entry<String, List<PNMessageResult>> entry : result.getChannels().entrySet()) {
                            assertTrue(channels.contains(entry.getKey()));
                            assertEquals(messagesCount, entry.getValue().size());
                            for (PNMessageResult pnMessageResult : entry.getValue()) {
                                assertTrue(channels.contains(pnMessageResult.getChannel()));
                            }
                        }

                        pastMessagesSuccess.set(true);
                        // end::ignore[]
                        if (!status.isError()) {
                            for (Map.Entry<String, List<PNMessageResult>> entry : result.getChannels().entrySet()) {
                                Log.i("batch_history", "Channel: " + entry.getKey());
                                for (PNMessageResult message : entry.getValue()) {
                                    Log.i("batch_history", "\tMessage: " + message.getMessage());
                                }
                                Log.i("batch_history", "-----\n");
                            }
                        }
                    }
                });
        // end::HIST-4[]
        Awaitility.await().atMost(TIMEOUT_LONG, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
    }

    interface Callback {

        void onDone();
    }

    private void publishMessages(String channel, int count, Callback callback) throws PubNubException,
            InterruptedException {
        for (int i = 0; i < count; i++) {
            pubNub.publish()
                    .channel(channel)
                    .message("#" + (i + 1) + " " + UUID.randomUUID())
                    .shouldStore(true)
                    .sync();
        }
        TimeUnit.SECONDS.sleep(TIMEOUT_SHORT);
        callback.onDone();
    }

}
