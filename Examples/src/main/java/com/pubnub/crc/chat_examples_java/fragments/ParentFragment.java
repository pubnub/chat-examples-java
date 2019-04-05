package com.pubnub.crc.chat_examples_java.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pubnub.crc.chat_examples_java.util.ParentActivityImpl;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract class ParentFragment extends Fragment {

    private static final String TAG = "ParentFragment";

    Context fragmentContext;
    private Unbinder mUnbinder;

    public abstract int provideLayoutResourceId();

    public abstract void setViewBehaviour();

    public abstract String setScreenTitle();

    public abstract void setupData(@Nullable Bundle savedInstanceState);

    public void extractArguments() {
    }

    ParentActivityImpl hostActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(provideLayoutResourceId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewBehaviour();
        setupData(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        hostActivity.setTitle(setScreenTitle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            extractArguments();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentContext = context;
        this.hostActivity = (ParentActivityImpl) context;
    }

    @Override
    public void onDetach() {
        this.fragmentContext = null;
        super.onDetach();
    }

    void runOnUiThread(Runnable runnable) {
        ((Activity) fragmentContext).runOnUiThread(runnable);
    }

}
