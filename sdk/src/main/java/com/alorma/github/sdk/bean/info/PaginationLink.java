package com.alorma.github.sdk.bean.info;

import android.net.Uri;

/**
 * Created by Bernat on 13/07/2014.
 */
public class PaginationLink {

    public Uri uri;
    public RelType rel;
    public int page;

    public PaginationLink(String link) {
        link = link.trim().replace("<", "").replace(">", "").replace("\"", "");
        String url = link.split(";")[0];
        String rel = link.split("rel=")[1];

        this.uri = Uri.parse(url);

        String page = this.uri.getQueryParameter("page");

        if (page != null) {
            this.page = Integer.valueOf(page);
        }

        if (RelType.next.toString().equals(rel)) {
            this.rel = RelType.next;
        } else if (RelType.last.toString().equals(rel)) {
            this.rel = RelType.last;
        } else if (RelType.first.toString().equals(rel)) {
            this.rel = RelType.first;
        } else if (RelType.prev.toString().equals(rel)) {
            this.rel = RelType.prev;
        }
    }

}
