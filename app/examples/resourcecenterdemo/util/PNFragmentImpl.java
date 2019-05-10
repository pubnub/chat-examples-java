package resourcecenterdemo.util;

import com.pubnub.api.callbacks.SubscribeCallback;

// tag::FRG-3[]
public interface PNFragmentImpl {

    SubscribeCallback provideListener();
}
// end::FRG-3[]
