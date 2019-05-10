package resourcecenterdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.enums.PNReconnectionPolicy;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import resourcecenterdemo.fragments.ChatFragment;
import resourcecenterdemo.prefs.Prefs;
import resourcecenterdemo.util.ParentActivityImpl;

// tag::INIT-3.1[]
// tag::ignore[]
/*
// end::ignore[]
public class MainActivity extends ParentActivity implements ParentActivityImpl {
// tag::ignore[]
*/
// end::ignore[]
// end::INIT-3.1[]

public class MainActivity extends ParentActivity implements ParentActivityImpl {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.container)
    FrameLayout mFragmentContainer;

    // tag::INIT-1.1[]
    private PubNub mPubNub; // a field of MainActivity.java
    // end::INIT-1.1[]

    final String channel = "demo-animal-forest";

    // tag::INIT-3.2[]
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ...
        // tag::ignore[]
        setSupportActionBar(mToolbar);
        initializePubNub();
        addFragment(ChatFragment.newInstance(channel));
        // end::ignore[]
    }
    // end::INIT-3.2[]

    @Override
    protected int provideLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void initializePubNub() {
        // tag::KEYS-2[]
        String pubKey = BuildConfig.PUB_KEY;
        String subKey = BuildConfig.SUB_KEY;
        // end::KEYS-2[]

        // tag::INIT-1.2[]
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(pubKey);
        pnConfiguration.setSubscribeKey(subKey);
        pnConfiguration.setUuid(Prefs.get().uuid()); // uuid is stored in SharedPreferences
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);
        pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pnConfiguration.setMaximumReconnectionRetries(10);

        mPubNub = new PubNub(pnConfiguration);
        // end::INIT-1.2[]
    }

    // tag::INIT-3.3[]
    @Override
    public PubNub getPubNub() {
        return mPubNub;
    }
    // end::INIT-3.3[]

    @Override
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void setSubtitle(String subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            mToolbar.setSubtitle(subtitle);
        }
    }

    @Override
    public void addFragment(Fragment fragment) {
        super.addFragment(fragment);
    }

    @Override
    public void enableBackButton(boolean enable) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        getSupportActionBar().setDisplayShowHomeEnabled(enable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPress();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void backPress() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if ((getSupportFragmentManager().getBackStackEntryCount() <= 1)) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    // tag::INIT-4[]
    @Override
    protected void onDestroy() {
        mPubNub.unsubscribeAll();
        mPubNub.forceDestroy();
        super.onDestroy();
    }
    // end::INIT-4[]
    // tag::INIT-3.4[]
}
// end::INIT-3.4[]
