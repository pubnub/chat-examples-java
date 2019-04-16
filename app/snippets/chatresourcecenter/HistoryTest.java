package chatresourcecenter;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HistoryTest extends TestHarness {

    @Test
    public void testFetchMessageCount() {
        final AtomicBoolean messageCountSuccess = new AtomicBoolean(false);
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(messageCountSuccess);
        // tag::HIST-1[]
        // in progress
        // end::HIST-1[]
    }

    @Test
    public void testRetrievePastMessages() {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
        // tag::HIST-2[]
        // in progress
        // end::HIST-2[]
    }

    @Test
    public void testRetrieveMoreThan100Messages() {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
        // tag::HIST-3[]
        // in progress
        // end::HIST-3[]
    }

    @Test
    public void testRetrieveMessagesMultiChannel() {
        final AtomicBoolean pastMessagesSuccess = new AtomicBoolean(false);
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(pastMessagesSuccess);
        // tag::HIST-4[]
        // in progress
        // end::HIST-4[]
    }

}
