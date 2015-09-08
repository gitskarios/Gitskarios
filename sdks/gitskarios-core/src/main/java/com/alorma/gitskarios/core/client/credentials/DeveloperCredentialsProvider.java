package com.alorma.gitskarios.core.client.credentials;

/**
 * Created by Bernat on 07/07/2015.
 */
public interface DeveloperCredentialsProvider {
    String getApiClient();
    String getAPiSecret();
    String getApiOauth();
}
