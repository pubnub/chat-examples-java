package chatresourcecenter;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EncryptionTest extends TestHarness {

    @Test
    public void testEnableEncryption() {
        // tag::ENCR-1[]
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        pnConfiguration.setSecure(true);

        PubNub pubNub = new PubNub(pnConfiguration);
        // end::ENCR-1[]

        assertNotNull(pubNub);
        assertTrue(pubNub.getConfiguration().isSecure());
    }

    @Test
    public void testCipherKey() throws PubNubException {
        final String expectedCipherKey = UUID.randomUUID().toString();
        final String expectedString = UUID.randomUUID().toString();

        // tag::ENCR-2[]
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUB_KEY);
        pnConfiguration.setPublishKey(PUB_KEY);
        // tag::ignore[]
        /*
        // end::ignore[]
        pnConfiguration.setCipherKey("myCipherKey");
        // tag::ignore[]
        */
        // end::ignore[]
        // tag::ignore[]
        pnConfiguration.setCipherKey(expectedCipherKey);
        // end::ignore[]

        PubNub pubNub = new PubNub(pnConfiguration);
        // end::ENCR-2[]

        assertNotNull(pubNub);
        assertEquals(expectedCipherKey, pubNub.getConfiguration().getCipherKey());

        String encrypted = pubNub.encrypt(expectedString, expectedCipherKey);
        String decrypted = pubNub.decrypt(encrypted, expectedCipherKey);

        assertEquals(expectedString, decrypted);
    }

}
