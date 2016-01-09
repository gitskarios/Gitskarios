package com.alorma.github;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Bernat on 02/04/2015.
 */
public class AccountsHelper {
    public static final String USER_PIC = "USER_PIC";
    public static final String USER_URL = "USER_URL";
    private static final String USER_MAIL = "USER_MAIL";
    private static final String USER_NAME = "USER_NAME";

    public static Bundle buildBundle(String name, String mail, String avatar) {
        Bundle userData = new Bundle();

        userData.putString(AccountsHelper.USER_PIC, avatar);
        userData.putString(AccountsHelper.USER_MAIL, mail);
        userData.putString(AccountsHelper.USER_NAME, name);

        return userData;
    }

    public static Bundle buildBundle(String name, String mail, String avatar, String url) {
        Bundle userData = new Bundle();

        userData.putString(AccountsHelper.USER_PIC, avatar);
        userData.putString(AccountsHelper.USER_MAIL, mail);
        userData.putString(AccountsHelper.USER_NAME, name);

        if (url != null) {
            userData.putString(AccountsHelper.USER_URL, url);
        }

        return userData;
    }

    public static String getUserAvatar(Context context, Account account) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(account, USER_PIC);
    }

    public static String getUserMail(Context context, Account account) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(account, USER_MAIL);
    }

    public static String getUrl(Context context, Account account) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(account, USER_URL);
    }

    public static String getUserName(Context context, Account account) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(account, USER_NAME);
    }

    public static String getUserToken(Context context, Account account) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(account, AccountManager.KEY_AUTHTOKEN);
    }
}
