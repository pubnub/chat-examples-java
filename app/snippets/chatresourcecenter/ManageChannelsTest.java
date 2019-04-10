package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ManageChannelsTest extends TestHarness {

    @Test
    public void testJoiningSingleChannel() {
        final AtomicBoolean joinSuccess = new AtomicBoolean(false);

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() == PNOperationType.PNSubscribeOperation
                        && status.getAffectedChannels().contains("room-1")) {
                    // tag::CHAN-1[]
                    pubNub.subscribe()
                            .channels(Arrays.asList("room-1"))
                            .execute();
                    // end::CHAN-1[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("join")
                        && presence.getChannel().equals("room-1")
                        && presence.getUuid().equals(pubNub.getConfiguration().getUuid())) {
                    joinSuccess.set(true);
                }
            }
        });

        observerClient.subscribe()
                .channels(Collections.singletonList("room-1"))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(joinSuccess);
    }

    @Test
    public void testJoiningMultipleChannels() throws PubNubException, InterruptedException {
        // tag::CHAN-2[]
        pubNub.subscribe()
                .channels(Arrays.asList("room-1", "room-2", "room-3"))
                .execute();
        // end::CHAN-2[]

        TimeUnit.SECONDS.sleep(TIMEOUT);

        PNHereNowResult hereNowResult = pubNub.hereNow()
                .channels(Arrays.asList("room-1", "room-2", "room-3"))
                .includeUUIDs(true)
                .sync();

        List<String> expectedChannels = Arrays.asList("room-1", "room-2", "room-3");

        assertNotNull(hereNowResult);
        assertEquals(expectedChannels.size(), hereNowResult.getTotalChannels());

        List<String> actualChanels = new ArrayList<>();

        for (Map.Entry<String, PNHereNowChannelData> entry : hereNowResult.getChannels().entrySet()) {
            actualChanels.add(entry.getKey());
            boolean member = false;
            for (PNHereNowOccupantData occupant : entry.getValue().getOccupants()) {
                if (occupant.getUuid().equals(pubNub.getConfiguration().getUuid())) {
                    member = true;
                    break;
                }
            }
            assertTrue(member);
        }

        assertArrayEquals(new List[]{expectedChannels}, new List[]{actualChanels});
    }

    @Test
    public void testLeave() {

        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getAffectedChannels().contains("room-1")) {
                    if (status.getOperation() == PNOperationType.PNSubscribeOperation) {
                        // tag::CHAN-3[]
                        pubNub.unsubscribe()
                                .channels(Arrays.asList("room-1"))
                                .execute();
                        // end::CHAN-3[]
                    } else if (status.getOperation() == PNOperationType.PNUnsubscribeOperation
                            && status.getUuid().equals(pubNub.getConfiguration().getUuid())) {
                        leaveSuccess.set(true);
                    }
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubNub.subscribe()
                .channels(Arrays.asList("room-1"))
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(leaveSuccess);
    }

    @Test
    public void testJoinChannelGroup() {
        final AtomicBoolean joinSuccess = new AtomicBoolean(false);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                joinSuccess.set(PnUtils.isSubscribedGroup(status, "family"));

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        // tag::CHAN-4[]
        pubNub.subscribe()
                .channelGroups(Arrays.asList("family"))
                .execute();
        // tag::CHAN-4[]

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(joinSuccess);

    }

}
