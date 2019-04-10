package resourcecenterdemo.util;

import com.pubnub.api.PubNub;

import androidx.fragment.app.Fragment;

public interface ParentActivityImpl {

    PubNub getPubNub();

    void setTitle(String title);

    void addFragment(Fragment fragment);

}
