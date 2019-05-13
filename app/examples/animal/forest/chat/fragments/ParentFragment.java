package animal.forest.chat.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import animal.forest.chat.util.PNFragmentImpl;
import animal.forest.chat.util.ParentActivityImpl;

abstract class ParentFragment extends Fragment implements PNFragmentImpl {

    private final String TAG = "PF_" + getClass().getSimpleName();

    Context fragmentContext;
    private Unbinder mUnbinder;
    private boolean mIsFromCache;
    private View rootView;

    public abstract int provideLayoutResourceId();

    public abstract void setViewBehaviour(boolean viewFromCache);

    public abstract String setScreenTitle();

    public abstract void onReady();

    public void extractArguments() {
    }

    // tag::FRG-1.1[]
    ParentActivityImpl hostActivity; // field of fragment
    // end::FRG-1.1[]

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (rootView != null) {
            mIsFromCache = true;
            return rootView;
        } else {
            rootView = getView();
            if (rootView == null) {
                rootView = inflater.inflate(provideLayoutResourceId(), container, false);
                mUnbinder = ButterKnife.bind(this, rootView);
                mIsFromCache = false;
            } else {
                mIsFromCache = true;
            }
        }
        Log.d(TAG, "onCreateView " + mIsFromCache);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "setViewBehaviour, cache: " + mIsFromCache);
        setViewBehaviour(mIsFromCache);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        hostActivity.setTitle(setScreenTitle());
    }

    // tag::FRG-5.1[]
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // tag::ignore[]
        Log.d(TAG, "onCreate");
        // end::ignore[]
        super.onCreate(savedInstanceState);
        // tag::ignore[]
        Log.d(TAG, "onReady");
        onReady();
        // end::ignore[]
        hostActivity.getPubNub().addListener(provideListener());
        // tag::ignore[]
        if (getArguments() != null) {
            extractArguments();
        }
        // end::ignore[]
    }
    // end::FRG-5.1[]

    // tag::FRG-1.2[]
    @Override
    public void onAttach(Context context) {
        // tag::ignore[]
        Log.d(TAG, "onAttach");
        // end::ignore[]
        super.onAttach(context);
        // tag::ignore[]
        this.fragmentContext = context;
        // end::ignore[]
        this.hostActivity = (ParentActivityImpl) context;
    }
    // end::FRG-1.2[]

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        this.fragmentContext = null;
        this.rootView = null;
        super.onDetach();
    }

    // tag::FRG-5.2[]
    @Override
    public void onDestroy() {
        // tag::ignore[]
        Log.d(TAG, "onDestroy");
        // end::ignore[]
        hostActivity.getPubNub().removeListener(provideListener());
        super.onDestroy();
    }
    // end::FRG-5.2[]

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    void runOnUiThread(Runnable runnable) {
        ((Activity) fragmentContext).runOnUiThread(runnable);
    }

}
