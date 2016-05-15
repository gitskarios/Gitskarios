package com.alorma.github.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Utility static methods for keyboard related functions.
 * <p>
 * Created by Daniel Watson on 5/14/16.
 */
public class KeyboardUtils {

    /**
     * Lower a keyboard owned by an activity window.
     *
     * @param activity Current visible instance of activity window
     */
    public static void lowerKeyboard(@NonNull Activity activity) {
        try {
            if (activity.getWindow() != null) {
                View decorView = activity.getWindow().getDecorView();
                lowerKeyboard(activity, decorView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lower an IEM keyboard that has been raised with a view such as dialog.
     *
     * @param applicationContext Context of application
     * @param focusView          View that owns keyboard.
     */
    public static void lowerKeyboard(@NonNull Context applicationContext, View focusView) {
        try {
            if (focusView != null && focusView.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager)
                        applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
