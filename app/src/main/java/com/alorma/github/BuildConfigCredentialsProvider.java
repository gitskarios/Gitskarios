package com.alorma.github;

import com.alorma.github.basesdk.client.GithubDeveloperCredentialsProvider;

/**
 * Created by Bernat on 07/07/2015.
 */
public class BuildConfigCredentialsProvider implements GithubDeveloperCredentialsProvider {
    @Override
    public String getApiClient() {
        return BuildConfig.CLIENT_ID;
    }

    @Override
    public String getAPiSecret() {
        return BuildConfig.CLIENT_SECRET;
    }

    @Override
    public String getApiOauth() {
        return BuildConfig.CLIENT_CALLBACK;
    }
}
