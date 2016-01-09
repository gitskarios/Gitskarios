package com.alorma.github.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;

/**
 * Created by a557114 on 02/09/2015.
 */
public class ShortcutUtils {

    public static void addShortcut(Context context, RepoInfo repoInfo) {
        if (repoInfo != null) {
            Intent intent = RepoDetailActivity.createShortcutLauncherIntent(context, repoInfo);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT").putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                    .putExtra(Intent.EXTRA_SHORTCUT_NAME, repoInfo.name)
                    .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
            context.sendBroadcast(addIntent);

            Toast.makeText(context, R.string.repo_add_to_homescreen, Toast.LENGTH_SHORT).show();
        }
    }

    public static void addShortcut(Context context, IssueInfo issueInfo) {
        if (issueInfo != null && issueInfo.repoInfo != null) {
            Intent intent = IssueDetailActivity.createShortcutLauncherIntent(context, issueInfo);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT").putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                    .putExtra(Intent.EXTRA_SHORTCUT_NAME, "#" + issueInfo.num + " " + issueInfo.repoInfo.name)
                    .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
            context.sendBroadcast(addIntent);

            Toast.makeText(context, R.string.repo_add_to_homescreen, Toast.LENGTH_SHORT).show();
        }
    }

    public static void addPrShortcut(Context context, IssueInfo issueInfo) {
        if (issueInfo != null && issueInfo.repoInfo != null) {
            Intent intent = PullRequestDetailActivity.createShortcutLauncherIntent(context, issueInfo);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT").putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                    .putExtra(Intent.EXTRA_SHORTCUT_NAME, "#" + issueInfo.num + " " + issueInfo.repoInfo.name)
                    .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
            context.sendBroadcast(addIntent);

            Toast.makeText(context, R.string.repo_add_to_homescreen, Toast.LENGTH_SHORT).show();
        }
    }
}
