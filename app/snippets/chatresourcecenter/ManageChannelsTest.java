package chatresourcecenter;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsAddChannelResult;
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsAllChannelsResult;
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsRemoveChannelResult;
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

import chatresourcecenter.mock.Log;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ManageChannelsTest extends TestHarness {

    @Test
    public void testJoiningSingleChannel() {
        final AtomicBoolean joinSuccess = new AtomicBoolean(false);

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, "room-1")) {
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
                if (PnUtils.checkPresence(presence, getUuid(), "join", "room-1")) {
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

    @Test
    public void testAddChannelsToChannelGroup() throws PubNubException {
        final AtomicBoolean addSuccess = new AtomicBoolean(false);

        assertNotNull(removeChannelsFromChannelGroup("family", "son", "daughter"));

        // tag::CHAN-5[]
        pubNub.addChannelsToChannelGroup()
                .channelGroup("family")
                .channels(Arrays.asList("son", "daughter"))
                .async(new PNCallback<PNChannelGroupsAddChannelResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsAddChannelResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        pubNub.listChannelsForChannelGroup()
                                .channelGroup("family")
                                .async(new PNCallback<PNChannelGroupsAllChannelsResult>() {
                                    @Override
                                    public void onResponse(PNChannelGroupsAllChannelsResult result, PNStatus status) {
                                        assertFalse(status.isError());
                                        assertTrue(result.getChannels().contains("son"));
                                        assertTrue(result.getChannels().contains("daughter"));
                                        addSuccess.set(true);
                                    }
                                });
                        // tag::ignore[]
                    }
                });
        // end::CHAN-5[]
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(addSuccess);
    }

    @Test
    public void testRemoveChannelsFromChannelGroup() throws PubNubException {
        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);

        assertNotNull(addChannelsToChannelGroup("family", "son"));

        PNChannelGroupsAllChannelsResult familyChannelGroup = getChannelsForChannelGroup("family");
        assertNotNull(familyChannelGroup);
        assertTrue(familyChannelGroup.getChannels().contains("son"));

        // tag::CHAN-6[]
        pubNub.removeChannelsFromChannelGroup()
                .channels(Arrays.asList("son"))
                .channelGroup("family")
                .async(new PNCallback<PNChannelGroupsRemoveChannelResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsRemoveChannelResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        try {
                            assertFalse(getChannelsForChannelGroup("family").getChannels().contains("son"));
                            leaveSuccess.set(true);
                        } catch (PubNubException e) {
                            e.printStackTrace();
                            leaveSuccess.set(false);
                        }
                        // tag::ignore[]
                    }
                });
        // end::CHAN-6[]

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(leaveSuccess);
    }

    @Test
    public void testListingChannelsInChannelGroup() throws PubNubException {
        final AtomicBoolean listingSuccess = new AtomicBoolean(false);

        assertNotNull(removeChannelsFromChannelGroup("family", "daughter"));
        assertNotNull(addChannelsToChannelGroup("family", "daughter"));

        // tag::CHAN-7[]
        pubNub.listChannelsForChannelGroup()
                .channelGroup("family")
                .async(new PNCallback<PNChannelGroupsAllChannelsResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsAllChannelsResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        assertNotNull(result);
                        assertTrue(result.getChannels().contains("daughter"));
                        listingSuccess.set(true);
                        // tag::ignore[]
                        if (!status.isError()) {
                            for (String channel : result.getChannels()) {
                                Log.d("channel", channel);
                            }
                        }
                    }
                });
        // end::CHAN-7[]

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(listingSuccess);
    }

    @Test
    public void testLeaveChannelGroup() {
        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribedGroup(status, "family")) {
                    // tag::CHAN-8[]
                    pubNub.unsubscribe()
                            .channelGroups(Arrays.asList("family"))
                            .execute();
                    // end::CHAN-8[]
                } else if (PnUtils.isUnsubscribedGroup(status, "family", pubNub.getConfiguration().getUuid())) {
                    leaveSuccess.set(true);
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
                .channelGroups(Arrays.asList("family"))
                .execute();

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).untilTrue(leaveSuccess);
    }

    private PNChannelGroupsAllChannelsResult getChannelsForChannelGroup(String channelGroup) throws PubNubException {
        return pubNub.listChannelsForChannelGroup()
                .channelGroup(channelGroup)
                .sync();
    }

    private PNChannelGroupsAddChannelResult addChannelsToChannelGroup(String channelGroup, String... channels) throws PubNubException {
        return pubNub.addChannelsToChannelGroup()
                .channelGroup(channelGroup)
                .channels(Arrays.asList(channels))
                .sync();
    }

    private PNChannelGroupsRemoveChannelResult removeChannelsFromChannelGroup(String channelGroup,
                                                                              String... channels) throws PubNubException {
        return pubNub.removeChannelsFromChannelGroup()
                .channelGroup(channelGroup)
                .channels(Arrays.asList(channels))
                .sync();
    }

}
