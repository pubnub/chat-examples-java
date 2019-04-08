package com.pubnub.crc.chat_examples_java.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pubnub.crc.chat_examples_java.util.ParentActivityImpl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract class ParentFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Context fragmentContext;
    private Unbinder mUnbinder;

    public abstract int provideLayoutResourceId();

    public abstract void setViewBehaviour();

    public abstract String setScreenTitle();

    public abstract void onReady();

    public void extractArguments() {
    }

    ParentActivityImpl hostActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = getView() != null ? getView() : inflater.inflate(provideLayoutResourceId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "setViewBehaviour");
        setViewBehaviour();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        onReady();
        hostActivity.setTitle(setScreenTitle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            extractArguments();
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        this.fragmentContext = context;
        this.hostActivity = (ParentActivityImpl) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        this.fragmentContext = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

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
