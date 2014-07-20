package com.alorma.github.sdk.bean.dto.response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Content extends ShaUrl{
    public ContentType type;
    public int size;
    public String name;
    public String path;
    public String git_url;
    public String html_url;
    public Link _links;
}
