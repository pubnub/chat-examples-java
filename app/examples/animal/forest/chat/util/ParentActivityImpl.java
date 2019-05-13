package animal.forest.chat.util;

import com.pubnub.api.PubNub;

import androidx.fragment.app.Fragment;

// tag::INIT-2[]
public interface ParentActivityImpl {

    PubNub getPubNub();

    // tag::ignore[]
    void setTitle(String title);

    void setSubtitle(String subtitle);

    void addFragment(Fragment fragment);

    void enableBackButton(boolean enable);

    void backPress();
    // end::ignore[]
}
// end::INIT-2[]
