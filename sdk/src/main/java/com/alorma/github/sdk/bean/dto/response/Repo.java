package com.alorma.github.sdk.bean.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Bernat on 13/07/2014.
 */
public class Repo {

    public boolean fork;

    public boolean hasDownloads;

    public boolean hasIssues;

    public boolean hasWiki;

    @SerializedName("private")
    public boolean isPrivate;

    public Date created_at;

    public Date pushed_at;

    public Date updated_at;

    public int forks_count;

    public long id;

    public Repo parent;

    public Repo source;

    public String clone_url;

    public String description;

    public String homepage;

    public String git_url;

    public String html_url;

    public String language;

    public String default_branch;

    public String mirror_url;

    public String name;

    public String full_name;

    public String ssh_url;

    public String svn_url;

    public String url;

    public User owner;

    public int stargazers_count;

    public int watchers_count;

    public int size;

    public int open_issues_count;

    public boolean has_issues;

    public boolean has_downloads;

    public Permissions permissions;


}
