package com.alorma.github.sdk.services;

import com.alorma.github.sdk.bean.dto.response.ListGists;

public interface OnGistResponseListener {
        void onGists(ListGists gists);
    }