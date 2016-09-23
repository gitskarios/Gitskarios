package com.alorma.github.sdk.core;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class GithubTest {

    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new Github();
    }

    @Test
    public void ShouldReturnOauthEndpoint() {
        String endpoint = apiClient.getApiOauthUrlEndpoint();

        assertThat(endpoint).isEqualTo("https://github.com");
    }

    @Test
    public void ShouldReturnApiEndpoint() {
        String endpoint = apiClient.getApiEndpoint();

        assertThat(endpoint).isEqualTo("https://api.github.com");
    }

    @Test
    public void ShouldReturnType() {
        String endpoint = apiClient.getType();

        assertThat(endpoint).isEqualTo("github");
    }

}