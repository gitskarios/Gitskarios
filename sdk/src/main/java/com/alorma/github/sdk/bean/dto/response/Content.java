package com.alorma.github.sdk.bean.dto.response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Content extends ShaUrl{
    public ContentType type;
    public int size;
    public String name;
    public String content;
    public String path;
    public String git_url;
    public String html_url;
    public Links _links;
    public String encoding;
    public ListContents children;
    public Content parent;

    public boolean isDir() {
        return ContentType.dir.equals(type);
    }
    public boolean isFile() {
        return ContentType.file.equals(type);
    }
}
