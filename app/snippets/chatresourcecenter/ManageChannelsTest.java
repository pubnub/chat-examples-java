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
        final String expectedChannel = randomUuid();

        observerClient.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribed(status, expectedChannel)) {
                    // tag::CHAN-1[]
                    pubNub.subscribe()
                            // tag::ignore[]
                            .channels(Arrays.asList(expectedChannel))
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channels(Arrays.asList("room-1"))
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .execute();
                    // end::CHAN-1[]
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (PnUtils.checkPresence(presence, getUuid(), "join", expectedChannel)) {
                    joinSuccess.set(true);
                }
            }
        });

        observerClient.subscribe()
                .channels(Collections.singletonList(expectedChannel))
                .withPresence()
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(joinSuccess);
    }

    @Test
    public void testJoiningMultipleChannels() throws PubNubException, InterruptedException {

        List<String> expectedChannels = new ArrayList<String>() {{
            add(randomUuid());
            add(randomUuid());
            add(randomUuid());
        }};

        Collections.sort(expectedChannels);

        // tag::CHAN-2[]
        pubNub.subscribe()
                // tag::ignore[]
                .channels(expectedChannels)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("room-1", "room-2", "room-3"))
                // tag::ignore[]
                */
                // end::ignore[]
                .execute();
        // end::CHAN-2[]

        TimeUnit.SECONDS.sleep(TIMEOUT_MEDIUM);

        PNHereNowResult hereNowResult = pubNub.hereNow()
                .channels(expectedChannels)
                .includeUUIDs(true)
                .sync();

        assertNotNull(hereNowResult);
        assertEquals(expectedChannels.size(), hereNowResult.getTotalChannels());

        List<String> actualChannels = new ArrayList<>();

        for (Map.Entry<String, PNHereNowChannelData> entry : hereNowResult.getChannels().entrySet()) {
            actualChannels.add(entry.getKey());
            boolean member = false;
            for (PNHereNowOccupantData occupant : entry.getValue().getOccupants()) {
                if (occupant.getUuid().equals(pubNub.getConfiguration().getUuid())) {
                    member = true;
                    break;
                }
            }
            assertTrue(member);
        }

        Collections.sort(actualChannels);

        assertArrayEquals(new List[]{expectedChannels}, new List[]{actualChannels});
    }

    @Test
    public void testLeave() {
        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        final String expectedChannel = randomUuid();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getAffectedChannels().contains(expectedChannel)) {
                    if (status.getOperation() == PNOperationType.PNSubscribeOperation) {
                        // tag::CHAN-3[]
                        pubNub.unsubscribe()
                                // tag::ignore[]
                                .channels(Arrays.asList(expectedChannel))
                                // end::ignore[]
                                // tag::ignore[]
                                /*
                                // end::ignore[]
                                .channels(Arrays.asList("room-1"))
                                // tag::ignore[]
                                */
                                // end::ignore[]
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
                .channels(Arrays.asList(expectedChannel))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(leaveSuccess);
    }

    @Test
    public void testJoinChannelGroup() {
        final AtomicBoolean joinSuccess = new AtomicBoolean(false);
        final String expectedChannelGroup = randomUuid();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                joinSuccess.set(PnUtils.isSubscribedGroup(status, expectedChannelGroup));

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
                // tag::ignore[]
                .channelGroups(Arrays.asList(expectedChannelGroup))
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channelGroups(Arrays.asList("family"))
                // tag::ignore[]
                */
                // end::ignore[]
                .execute();
        // end::CHAN-4[]

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(joinSuccess);
    }

    @Test
    public void testAddChannelsToChannelGroup() throws PubNubException {
        final AtomicBoolean addSuccess = new AtomicBoolean(false);

        String expectedChannelGroup = randomUuid();
        String expectedChannel1 = randomUuid();
        String expectedChannel2 = randomUuid();

        assertNotNull(removeChannelsFromChannelGroup(expectedChannelGroup, expectedChannel1, expectedChannel2));

        // tag::CHAN-5[]
        pubNub.addChannelsToChannelGroup()
                // tag::ignore[]
                .channelGroup(expectedChannelGroup)
                .channels(Arrays.asList(expectedChannel1, expectedChannel2))
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channelGroup("family")
                .channels(Arrays.asList("son", "daughter"))
                // tag::ignore[]
                */
                // end::ignore[]
                .async(new PNCallback<PNChannelGroupsAddChannelResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsAddChannelResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        pubNub.listChannelsForChannelGroup()
                                .channelGroup(expectedChannelGroup)
                                .async(new PNCallback<PNChannelGroupsAllChannelsResult>() {
                                    @Override
                                    public void onResponse(PNChannelGroupsAllChannelsResult result, PNStatus status) {
                                        assertFalse(status.isError());
                                        assertTrue(result.getChannels().contains(expectedChannel1));
                                        assertTrue(result.getChannels().contains(expectedChannel2));
                                        addSuccess.set(true);
                                    }
                                });
                        // end::ignore[]
                        // handle state setting response
                    }
                });
        // end::CHAN-5[]
        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(addSuccess);
    }

    @Test
    public void testRemoveChannelsFromChannelGroup() throws PubNubException {
        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        String expectedChannelGroup = randomUuid();
        String expectedChannel = randomUuid();

        assertNotNull(addChannelsToChannelGroup(expectedChannelGroup, expectedChannel));

        PNChannelGroupsAllChannelsResult familyChannelGroup = getChannelsForChannelGroup(expectedChannelGroup);
        assertNotNull(familyChannelGroup);
        assertTrue(familyChannelGroup.getChannels().contains(expectedChannel));

        // tag::CHAN-6[]
        pubNub.removeChannelsFromChannelGroup()
                // tag::ignore[]
                .channels(Arrays.asList(expectedChannel))
                .channelGroup(expectedChannelGroup)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channels(Arrays.asList("son"))
                .channelGroup("family")
                // tag::ignore[]
                */
                // end::ignore[]
                .async(new PNCallback<PNChannelGroupsRemoveChannelResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsRemoveChannelResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        try {
                            assertFalse(getChannelsForChannelGroup(expectedChannelGroup).getChannels()
                                    .contains(expectedChannel));
                            leaveSuccess.set(true);
                        } catch (PubNubException e) {
                            e.printStackTrace();
                            leaveSuccess.set(false);
                        }
                        // end::ignore[]
                    }
                });
        // end::CHAN-6[]

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(leaveSuccess);
    }

    @Test
    public void testListingChannelsInChannelGroup() throws PubNubException {
        final AtomicBoolean listingSuccess = new AtomicBoolean(false);
        String expectedChannelGroup = randomUuid();
        String expectedChannel = randomUuid();

        assertNotNull(removeChannelsFromChannelGroup(expectedChannelGroup, expectedChannel));
        assertNotNull(addChannelsToChannelGroup(expectedChannelGroup, expectedChannel));

        // tag::CHAN-7[]
        pubNub.listChannelsForChannelGroup()
                // tag::ignore[]
                .channelGroup(expectedChannelGroup)
                // end::ignore[]
                // tag::ignore[]
                /*
                // end::ignore[]
                .channelGroup("family")
                // tag::ignore[]
                */
                // end::ignore[]
                .async(new PNCallback<PNChannelGroupsAllChannelsResult>() {
                    @Override
                    public void onResponse(PNChannelGroupsAllChannelsResult result, PNStatus status) {
                        // tag::ignore[]
                        assertFalse(status.isError());
                        assertNotNull(result);
                        assertTrue(result.getChannels().contains(expectedChannel));
                        listingSuccess.set(true);
                        // end::ignore[]
                        if (!status.isError()) {
                            for (String channel : result.getChannels()) {
                                Log.i("channel", channel);
                            }
                        }
                    }
                });
        // end::CHAN-7[]

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(listingSuccess);
    }

    @Test
    public void testLeaveChannelGroup() {
        final AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        String expectedChannelGroup = randomUuid();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (PnUtils.isSubscribedGroup(status, expectedChannelGroup)) {
                    // tag::CHAN-8[]
                    pubNub.unsubscribe()
                            // tag::ignore[]
                            .channelGroups(Arrays.asList(expectedChannelGroup))
                            // end::ignore[]
                            // tag::ignore[]
                            /*
                            // end::ignore[]
                            .channelGroups(Arrays.asList("family"))
                            // tag::ignore[]
                            */
                            // end::ignore[]
                            .execute();
                    // end::CHAN-8[]
                } else if (PnUtils.isUnsubscribedGroup(status, expectedChannelGroup, getUuid())) {
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
                .channelGroups(Arrays.asList(expectedChannelGroup))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(leaveSuccess);
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
