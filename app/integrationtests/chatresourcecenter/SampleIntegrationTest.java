package chatresourcecenter;

import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import org.junit.Assert;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import androidx.test.runner.AndroidJUnit4;
import chatresourcecenter.util.TestHarness;

@RunWith(AndroidJUnit4.class)
public class SampleIntegrationTest extends TestHarness {

    public void testSample_1() throws InterruptedException {

        final CountDownLatch signal = new CountDownLatch(1);

        String channel = UUID.randomUUID().toString();
        String message = UUID.randomUUID().toString();

        pubNub.publish()
                .channel(channel)
                .message(message)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                        Assert.assertFalse(status.isError());

                        pubNub.history()
                                .channel(channel)
                                .async(new PNCallback<PNHistoryResult>() {
                                    @Override
                                    public void onResponse(PNHistoryResult result, PNStatus status) {
                                        Assert.assertFalse(status.isError());
                                        Assert.assertEquals(1, result.getMessages().size());
                                        Assert.assertEquals(message, result.getMessages()
                                                .get(0)
                                                .getEntry()
                                                .toString());

                                        signal.countDown();

                                    }
                                });
                    }
                });

        signal.await();
    }

}
