package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import chatresourcecenter.util.TestHarness;

@RunWith(AndroidJUnit4.class)
public class PresenceIntegrationTest extends TestHarness {

    @Test
    public void testHereNow() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final String channel = UUID.randomUUID().toString();

        List<String> userList = new ArrayList<>();
        userList.add("user_uuid_1");
        userList.add("user_uuid_2");
        userList.add("user_uuid_3");

        PubNub user1 = getPubNub(userList.get(0));
        PubNub user2 = getPubNub(userList.get(1));
        PubNub user3 = getPubNub(userList.get(2));

        subscribeToChannel(user1, channel);
        subscribeToChannel(user2, channel);
        subscribeToChannel(user3, channel);

        wait(1);

        pubNub.hereNow()
                .channels(Arrays.asList(channel))
                .includeUUIDs(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        int numberOfSubscribers = 0;
                        Assert.assertFalse(status.isError());

                        for (PNHereNowChannelData channelData : result.getChannels().values()) {
                            for (PNHereNowOccupantData pnHereNowOccupantData : channelData.getOccupants()) {
                                for (String s : userList) {
                                    if (pnHereNowOccupantData.getUuid().equals(s)) {
                                        numberOfSubscribers++;
                                    }
                                }
                            }
                        }
                        Assert.assertEquals(3, numberOfSubscribers);

                        user1.disconnect();
                        user1.destroy();
                        user2.disconnect();
                        user2.destroy();
                        user3.disconnect();
                        user3.destroy();

                        signal.countDown();
                    }
                });

        signal.await();
    }
}
