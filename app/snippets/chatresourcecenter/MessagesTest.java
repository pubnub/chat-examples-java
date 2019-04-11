package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

}
