package com.pubnub.crc.examples.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtils {

    private AndroidUtils() {
    }

    /**
     * Hides the soft-keyboard.
     *
     * @param view    The view which currently has the focus.
     * @param context The view context.
     */
    public static void hideKeyboard(View view, Context context) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String emphasizeText(String text) {
        return "<b>" + text + "</b>";
    }

    public static String newLine() {
        return "<br>";
    }
}
