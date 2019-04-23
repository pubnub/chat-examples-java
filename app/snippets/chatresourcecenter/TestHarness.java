package chatresourcecenter;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import org.junit.After;
import org.junit.Before;

import java.util.UUID;

import resourcecenterdemo.BuildConfig;

abstract class TestHarness {

    PubNub pubNub;
    PubNub observerClient;

    static final String SUB_KEY = BuildConfig.SUB_KEY;
    static final String PUB_KEY = BuildConfig.PUB_KEY;

    static final int TIMEOUT_SHORT = 2;
    static final int TIMEOUT_MEDIUM = 5;
    static final int TIMEOUT_LONG = 10;

    @Before
    public void before() {
        System.out.println("*** BEFORE ***");
        pubNub = getPubNubClient();
        observerClient = getPubNubClient();
    }

    @After
    public void after() {
        System.out.println("*** AFTER ***");
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
        /*pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);*/
        return pnConfiguration;
    }

    String getUuid() {
        return pubNub.getConfiguration().getUuid();
    }

    String randomUuid(){
        return UUID.randomUUID().toString();
    }



}
