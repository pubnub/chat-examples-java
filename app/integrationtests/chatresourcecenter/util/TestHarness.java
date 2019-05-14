package chatresourcecenter.util;

import android.os.SystemClock;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNHeartbeatNotificationOptions;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import org.junit.After;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import animal.forest.chat.BuildConfig;

abstract public class TestHarness {

    public PubNub pubNub;
    public PubNub observerClient;

    static final String SUB_KEY = BuildConfig.SUB_KEY;
    static final String PUB_KEY = BuildConfig.PUB_KEY;

    public static final int TIMEOUT_SHORT = 2;
    public static final int TIMEOUT_MEDIUM = 5;
    public static final int TIMEOUT_LONG = 10;

    @Before
    public void before() {
        pubNub = getPubNubClient();
        observerClient = getPubNubClient();
    }

    @After
    public void after() {
        destroyClient(pubNub);
        destroyClient(observerClient);
    }

    private void destroyClient(PubNub client) {
        client.unsubscribeAll();
        client.forceDestroy();
        client = null;
    }

    private PubNub getPubNubClient() {
        return new PubNub(getPnConfiguration());
    }

    private PNConfiguration getPnConfiguration() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        return pnConfiguration;
    }

    protected PubNub getPubNub(String uuid) {
        PNConfiguration pnbConfiguration = new PNConfiguration();
        pnbConfiguration.setSubscribeKey(SUB_KEY);
        pnbConfiguration.setPublishKey(PUB_KEY);
        pnbConfiguration.setSecure(true);
        pnbConfiguration.setUuid(uuid);
        pnbConfiguration.setHeartbeatNotificationOptions(PNHeartbeatNotificationOptions.ALL);
        return new PubNub(pnbConfiguration);
    }

    String getUuid() {
        return pubNub.getConfiguration().getUuid();
    }

    String randomUuid() {
        return UUID.randomUUID().toString();
    }

    protected void wait(int seconds) {
        SystemClock.sleep(seconds * 1000);
    }

    protected void subscribeToChannel(String channel) {
        pubNub.subscribe()
                .channels(Arrays.asList(channel))
                .withPresence()
                .execute();
    }

    protected void subscribeToChannel(PubNub pubnub, String channel) {
        pubnub.subscribe()
                .channels(Arrays.asList(channel))
                .withPresence()
                .execute();
    }

    protected void publishMessage(String channel, String message) {
        pubNub.publish()
                .message(generateMessage(pubNub, message))
                .channel(channel)
                .shouldStore(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {

                    }
                });
    }

    protected void publishMessages(String channel, int counter) {
        for (int i = 0; i < counter; i++) {
            publishMessage(channel, UUID.randomUUID().toString());
        }
    }

    protected Map generateMessage(PubNub pubNub, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("publisher", pubNub.getConfiguration().getUuid());
        map.put("text", message);

        return map;
    }
}
