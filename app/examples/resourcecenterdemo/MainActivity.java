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

public class MainActivity extends ParentActivity implements ParentActivityImpl {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.container)
    FrameLayout mFragmentContainer;

    private PubNub mPubNub;

    final String channel = "demo-animal-forest";
    final String historyChannel = "much_history";
    final String spareChannel = "apr19mo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        initializePubNub();
        addFragment(ChatFragment.newInstance(spareChannel));
    }

    @Override
    protected int provideLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void initializePubNub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(BuildConfig.PUB_KEY);
        pnConfiguration.setSubscribeKey(BuildConfig.SUB_KEY);
        pnConfiguration.setUuid(Prefs.get().uuid());
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);
        pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        mPubNub = new PubNub(pnConfiguration);
    }

    @Override
    public PubNub getPubNub() {
        return mPubNub;
    }

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
        // handle arrow click here
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

    @Override
    protected void onDestroy() {
        getPubNub().unsubscribeAll();
        getPubNub().forceDestroy();
        super.onDestroy();
    }
}
