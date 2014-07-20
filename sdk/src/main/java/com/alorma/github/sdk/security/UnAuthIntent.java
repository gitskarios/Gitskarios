package com.alorma.github.sdk.security;

import android.content.Intent;

/**
 * Created by Bernat on 19/07/2014.
 */
public class UnAuthIntent extends Intent {

    public static final String ACTION = "com.alorma.UNAUTHORIZED";

    public UnAuthIntent() {
        this.setAction(ACTION);
    }

}
