package chatresourcecenter;

import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import chatresourcecenter.mock.Log;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class HistoryTest extends TestHarness {

    interface Callback {

        void onDone();
    }

    void publishMessages(String channel, int count, Callback callback) throws PubNubException, InterruptedException {
        for (int i = 0; i < count; i++) {
            pubNub.publish()
                    .channel(channel)
                    .message("#" + (i + 1) + " " + UUID.randomUUID())
                    .shouldStore(true)
                    .sync();
        }
        TimeUnit.SECONDS.sleep(TIMEOUT_MEDIUM);
        callback.onDone();
    }

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
        String channel = UUID.randomUUID().toString();
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
                            for (PNHistoryItemResult message : result.getMessages()) {
                                System.out.println(message.getEntry());
                            }
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
        final String channel = UUID.randomUUID().toString();

        publishMessages(channel, publishMessageCount, () -> {
            getAllMessages(channel, null, resursiveHistoryCount);
            Awaitility.await()
                    .atMost(TIMEOUT_LONG, TimeUnit.SECONDS)
                    .untilAtomic(resursiveHistoryCount, equalTo(expectedHistoryCallCount));
        });
    }

    // tag::HIST-3[]
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
                                Log.d("messages", String.valueOf(messages.size()));
                                Log.d("messages", "start: " + start);
                                Log.d("messages", "end: " + end);
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
    // end::HIST-3[]

    @Test
    public void testRetrieveMessagesMultiChannel() {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);
        pastMessagesSuccess.set(true);
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
        // tag::HIST-4[]
        // in progress
        // end::HIST-4[]
    }

}
