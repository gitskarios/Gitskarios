package com.alorma.github.sdk.bean.dto.response;

import java.util.Date;

/**
 * Created by Bernat on 04/09/2014.
 */
public class Organization extends ShaUrl{

	public int id;
	public String login;
	public String name;
	public String company;

	public Date created_at;

	public String avatar_url;
	public String gravatar_id;
	public String blog;
	public String bio;
	public String email;

	public String location;
	public UserType type;

	public boolean site_admin;

	public int public_repos;
	public int public_gists;
	public int followers;
	public int following;
}
