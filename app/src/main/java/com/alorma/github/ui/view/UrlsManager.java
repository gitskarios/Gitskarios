package com.alorma.github.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;

import java.util.Arrays;

/**
 * Created by Bernat on 27/04/2015.
 */
public class UrlsManager {

    private static final int URI_BASE = 0;
    private static final int URI_USER = 1;
    private static final int URI_REPO = 2;
    private static final int URI_ISSUE = 3;
    private static final int URI_COMMIT = 4;
    private static final int URI_REPO_BRANCH = 5;
    private static final int URI_REPO_BRANCH_FEATURE = 6;
    private static final int URI_REPO_BRANCH_RELEASE = 7;
    private static final int URI_REPO_BRANCH_HOTFIX = 8;


    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private Context context;

    public UrlsManager(Context context) {
        this.context = context;
        int i = 1000;

        uriMatcher.addURI("github.com", "", URI_BASE + i);
        for (String key : context.getResources().getStringArray(R.array.reservedKeys)) {
            uriMatcher.addURI("github.com", key, UriMatcher.NO_MATCH + i++);
        }

        uriMatcher.addURI("github.com", "*", URI_USER);
        uriMatcher.addURI("github.com", "*/*", URI_REPO);
        uriMatcher.addURI("github.com", "*/*/tree/feature/*", URI_REPO_BRANCH_FEATURE);
        uriMatcher.addURI("github.com", "*/*/tree/release/*", URI_REPO_BRANCH_RELEASE);
        uriMatcher.addURI("github.com", "*/*/tree/hotfix/*", URI_REPO_BRANCH_HOTFIX);
        uriMatcher.addURI("github.com", "*/*/tree/*", URI_REPO_BRANCH);
        uriMatcher.addURI("github.com", "*/*/issues/#", URI_ISSUE);
        uriMatcher.addURI("github.com", "*/*/commit/*", URI_COMMIT);
    }

    public void manageUrls(final WebView webView) {
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = checkUrl(url);
                if (intent != null) {
                    view.getContext().startActivity(intent);
                }
                return intent != null;
            }
        });
    }

    public Intent checkUrl(String url) {

        Uri uri = Uri.parse(url);

        return checkUri(uri);
    }

    public Intent checkUri(Uri uri) {

        boolean matched = uriMatcher.match(uri) > -1 && uriMatcher.match(uri) < 1000;
        if (matched) {
            Intent intent = null;
            switch (uriMatcher.match(uri)) {
                case URI_REPO:
                case URI_REPO_BRANCH:
                case URI_REPO_BRANCH_FEATURE:
                case URI_REPO_BRANCH_RELEASE:
                case URI_REPO_BRANCH_HOTFIX:
                    intent = manageRepos(uri);
                    break;
                case URI_USER:
                    intent = manageUsers(uri);
                    break;
            }
            return intent;
        }

        return null;
    }

    public Intent manageRepos(String url) {
        return manageRepos(Uri.parse(url));
    }

    public Intent manageRepos(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_REPO:
            case URI_REPO_BRANCH:
            case URI_REPO_BRANCH_FEATURE:
            case URI_REPO_BRANCH_RELEASE:
            case URI_REPO_BRANCH_HOTFIX:
                RepoInfo repoInfo = extractRepo(uri);
                return RepoDetailActivity.createLauncherIntent(context, repoInfo);
        }
        return null;
    }

    private RepoInfo extractRepo(Uri uri) {
        RepoInfo repoInfo = new RepoInfo();

        repoInfo.owner = uri.getPathSegments().get(0);
        repoInfo.name = uri.getPathSegments().get(1);

        if (uriMatcher.match(uri) == URI_REPO_BRANCH) {
            repoInfo.branch = uri.getLastPathSegment();
        } else if (uriMatcher.match(uri) == URI_REPO_BRANCH_FEATURE || uriMatcher.match(uri) == URI_REPO_BRANCH_RELEASE || uriMatcher.match(uri) == URI_REPO_BRANCH_HOTFIX) {
            repoInfo.branch = uri.getPathSegments().get(3) + "/" + uri.getPathSegments().get(4);
        }

        return repoInfo;
    }

    public Intent manageUsers(String url) {
        return manageUsers(Uri.parse(url));
    }

    public Intent manageUsers(Uri uri) {
        User user = new User();
        user.login = uri.getLastPathSegment();
        return ProfileActivity.createLauncherIntent(context, user);
    }
}
