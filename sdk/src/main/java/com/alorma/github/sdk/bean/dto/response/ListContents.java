package com.alorma.github.sdk.bean.dto.response;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Bernat on 20/07/2014.
 */
public class ListContents extends ArrayList<Content> {

    public ListContents(int capacity) {
        super(capacity);
    }
    public ListContents() {
        super();
    }

    public static class SORT {
        public static Comparator<Content> TYPE = new Comparator<Content>() {
            @Override
            public int compare(Content content, Content content2) {
                if (content.type == ContentType.up) {
                    if (content2.type == ContentType.dir || content2.type == ContentType.file) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (content.type == ContentType.dir) {
                    if (content2.type == ContentType.dir) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (content.type == ContentType.submodule) {
                    if (content2.type == ContentType.submodule) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return 0;
            }
        };
    }
}
