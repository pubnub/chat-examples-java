package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class PresenceIntegrationTest extends TestHarness {

    @Test
    public void testHereNow() {
        final AtomicBoolean success = new AtomicBoolean(false);

        final String channel = randomUuid();

        List<String> userList = new ArrayList<>();
        userList.add(randomUuid());
        userList.add(randomUuid());
        userList.add(randomUuid());

        PubNub user1 = getPubNub(userList.get(0));
        PubNub user2 = getPubNub(userList.get(1));
        PubNub user3 = getPubNub(userList.get(2));

        subscribeToChannel(user1, channel);
        subscribeToChannel(user2, channel);
        subscribeToChannel(user3, channel);

        wait(TIMEOUT_SHORT);

        pubNub.hereNow()
                .channels(Arrays.asList(channel))
                .includeUUIDs(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        Assert.assertFalse(status.isError());

                        int numberOfSubscribers = 0;

                        Assert.assertEquals(1, result.getTotalChannels());
                        Assert.assertEquals(userList.size(), result.getTotalOccupancy());

                        for (PNHereNowChannelData channelData : result.getChannels().values()) {
                            for (PNHereNowOccupantData pnHereNowOccupantData : channelData.getOccupants()) {
                                for (String s : userList) {
                                    if (pnHereNowOccupantData.getUuid().equals(s)) {
                                        numberOfSubscribers++;
                                    }
                                }
                            }
                        }
                        Assert.assertEquals(userList.size(), numberOfSubscribers);

                        destroyClient(user1);
                        destroyClient(user2);
                        destroyClient(user3);

                        success.set(true);
                    }
                });

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(success);

    }
}
