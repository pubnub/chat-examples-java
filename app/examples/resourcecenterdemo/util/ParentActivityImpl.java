package resourcecenterdemo.util;

import com.pubnub.api.PubNub;

import androidx.fragment.app.Fragment;

public interface ParentActivityImpl {

    PubNub getPubNub();

    void setTitle(String title);

    void setSubtitle(String subtitle);

    void addFragment(Fragment fragment);

    void enableBackButton(boolean enable);

    void backPress();

}
