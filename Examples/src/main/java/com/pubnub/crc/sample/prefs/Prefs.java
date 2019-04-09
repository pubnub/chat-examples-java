package com.pubnub.crc.sample.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.pubnub.crc.sample.model.Users;
import com.pubnub.crc.sample.util.Helper;

public class Prefs {

    private static String TAG = "Prefs";

    private static Prefs sUniqueInstance;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "global_preferences";

    private static final String KEY_PUB = "pub";
    private static final String KEY_SUB = "sub";
    private static final String KEY_UUID = "uuid";

    private Prefs(Context appContext) {
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs get() {
        if (sUniqueInstance == null) {
            throw new IllegalStateException("Prefs is not initialized, call initialize method first.");
        }
        return sUniqueInstance;
    }

    public static void initialize(Context appContext) {
        if (appContext == null) {
            throw new NullPointerException("Provided application context is null");
        }
        if (sUniqueInstance == null) {
            sUniqueInstance = new Prefs(appContext);
        }
    }

    public String pubKey() {
        return prefs.getString(KEY_PUB, null);
    }

    @SuppressLint("ApplySharedPref")
    public void pubKey(String pubKey) {
        prefs.edit().putString(KEY_PUB, pubKey).commit();
    }

    public String subKey() {
        return prefs.getString(KEY_SUB, null);
    }

    @SuppressLint("ApplySharedPref")
    public void subKey(String subKey) {
        prefs.edit().putString(KEY_SUB, subKey).commit();
    }

    @SuppressLint("ApplySharedPref")
    public String uuid() {
        if (!prefs.contains(KEY_UUID)) {
            prefs.edit().putString(KEY_UUID, Helper.getRandomElement(Users.all()).getUuid()).commit();
        }
        return prefs.getString(KEY_UUID, null);
    }

    @SuppressLint("ApplySharedPref")
    public void uuid(String uuid) {
        prefs.edit().putString(KEY_UUID, uuid).commit();
    }

    @SuppressLint("ApplySharedPref")
    public void clearAllData() {
        prefs.edit().clear().commit();
    }
}
