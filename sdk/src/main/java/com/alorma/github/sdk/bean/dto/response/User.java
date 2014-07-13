package com.alorma.github.sdk.bean.dto.response;

import java.util.Date;

public class User {

    public int id;
    public String login;
    public String name;
    public String company;

    public Date created_at;
    public Date updated_at;

    public boolean hireable;
    public String avatar_url;
    public String gravatar_id;
    public String blog;
    public String bio;
    public String email;

    public String location;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;

    public String received_events_url;
    public UserType type;

    public boolean site_admin;

    public int public_repos;
    public int public_gists;
    public int owned_public_repos;
    public int total_public_repos;
    public int followers;
    public int following;
    public int collaborators;
    public int disk_usage;
    
    public UserPlan plan;


}