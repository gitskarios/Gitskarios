package com.alorma.github.utils.uris;

import android.net.Uri;

/**
 * Created by Bernat on 18/04/2015.
 */
public class RepoUri {
    private Uri uri;

    public RepoUri create(String url) {
        uri = Uri.parse(url);
        return this;
    }

    public String getOwner() {
        if (uri != null) {
            int path = 0;
            if (uri.getAuthority().contains("api")) {
                path = 1;
            }
            return uri.getPathSegments().get(path);
        }
        return null;
    }

    public String getRepo() {
        if (uri != null) {
            int path = 1;
            if (uri.getAuthority().contains("api")) {
                path = 2;
            }
            return uri.getPathSegments().get(path);
        }
        return null;
    }
}
