package chatresourcecenter;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import chatresourcecenter.util.TestHarness;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PresenceEventsIntegrationTests extends TestHarness {

    @Test
    public void testJoinChannel() {
        final String channel = UUID.randomUUID().toString();
        final AtomicBoolean atomic = new AtomicBoolean(false);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("join")) {
                    Assert.assertEquals(channel, presence.getChannel());
                    pubnub.removeListener(this);
                    atomic.set(true);
                }
            }
        });

        subscribeToChannel(channel);

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(atomic);
    }

    @Test
    public void testLeaveChannel() {
        final AtomicBoolean atomic = new AtomicBoolean(false);
        final String channel = UUID.randomUUID().toString();

        PubNub guestUser = getPubNub("guest-" + UUID.randomUUID().toString());

        subscribeToChannel(channel);
        subscribeToChannel(guestUser, channel);

        wait(1);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("leave")) {
                    assertEquals(channel, presence.getChannel());
                    atomic.set(true);
                }
            }
        });

        guestUser.unsubscribe()
                .channels(Arrays.asList(channel))
                .execute();

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(atomic);
    }

    @Test
    public void testTimeoutFromChannel() {
        final AtomicBoolean atomic = new AtomicBoolean(false);
        pubNub.getConfiguration().setPresenceTimeout(2);

        final String channel = UUID.randomUUID().toString();

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("timeout")) {
                    Assert.assertEquals(channel, presence.getChannel());
                    pubnub.removeListener(this);
                    atomic.set(true);
                }
            }
        });

        subscribeToChannel(channel);

        Awaitility.await().atMost(TIMEOUT_LONG, TimeUnit.SECONDS).untilTrue(atomic);
    }

    @Test
    public void testStateChangeEvent() {
        final AtomicBoolean atomic = new AtomicBoolean(false);
        final String channel = UUID.randomUUID().toString();

        JsonObject state = new JsonObject();
        state.addProperty("is_typing", true);

        subscribeToChannel(channel);

        wait(1);

        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                if (presence.getEvent().equals("state-change") && presence.getUuid().equals(pubnub.getConfiguration().getUuid())) {
                    Assert.assertEquals("state-change", presence.getEvent());
                    pubnub.removeListener(this);
                    atomic.set(true);
                }
            }
        });

        pubNub.setPresenceState()
                .channels(Arrays.asList(channel))
                .state(state)
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(PNSetStateResult result, PNStatus status) {

                    }
                });

        Awaitility.await().atMost(TIMEOUT_MEDIUM, TimeUnit.SECONDS).untilTrue(atomic);
    }
}
