package com.alorma.github;

import android.content.Context;

/**
 * Created by Bernat on 26/09/2014.
 */
public class GitskariosSettings extends PreferencesHelper {
    public static final String KEY_REPO_SORT = "KEY_REPO_SORT";
    public static final String KEY_MARK_AS_READ = "KEY_MARK_AS_READ";
    public static final String KEY_ISSUE_EDIT = "KEY_ISSUE_EDIT";
    public static final String KEY_VERSION = "KEY_VERSION";
    public static final String KEY_DOWNLOAD_FILE_TYPE = "KEY_DOWNLOAD_FILE_TYPE";
    private static final String KEY_SHOW_ENTERPRISE = "KEY_SHOW_ENTERPRISE";

    public GitskariosSettings(Context context) {
        super(context);
    }

    public void saveRepoSort(String value) {
        saveStringSetting(KEY_REPO_SORT, value);
    }

    public String getRepoSort(String defaultValue) {
        return getStringSetting(KEY_REPO_SORT, defaultValue);
    }

    public String getDownloadFileType(String defaultType) {
        return getStringSetting(KEY_DOWNLOAD_FILE_TYPE, defaultType);
    }

    public void saveDownloadFileType(String downloadFileType) {
        saveStringSetting(KEY_DOWNLOAD_FILE_TYPE, downloadFileType);
    }

    public void saveVersion(int currentVersion) {
        saveIntSetting(KEY_VERSION, currentVersion);
    }

    public int getVersion(int currentVersion) {
        return getIntSetting(KEY_VERSION, currentVersion);
    }

    public boolean shouldShowDialogEditIssue() {
        boolean result = getBooleanSetting(KEY_ISSUE_EDIT, true);

        saveBooleanSetting(KEY_ISSUE_EDIT, false);

        return result;
    }

    public void saveMarkAsRead(boolean value) {
        saveBooleanSetting(KEY_MARK_AS_READ, value);
    }

    public boolean markAsRead() {
        return getBooleanSetting(KEY_MARK_AS_READ, false);
    }

    public boolean getShowEnterprise() {
        return getBooleanSetting(KEY_SHOW_ENTERPRISE, true);
    }

    public void setShowEnterpriseVisited() {
        saveBooleanSetting(KEY_SHOW_ENTERPRISE, false);
    }
}
