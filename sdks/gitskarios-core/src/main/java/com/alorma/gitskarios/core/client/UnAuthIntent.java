package com.alorma.gitskarios.core.client;

import android.content.Intent;

/**
 * Created by Bernat on 19/07/2014.
 */
public class UnAuthIntent extends Intent {

    public static final String ACTION = "com.alorma.UNAUTHORIZED";
    public static final String TOKEN = "TOKEN";

    public UnAuthIntent(String token) {
        this.setAction(ACTION);
        this.putExtra(TOKEN, token);
    }

}
