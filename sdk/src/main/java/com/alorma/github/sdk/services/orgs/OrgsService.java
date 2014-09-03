package com.alorma.github.sdk.services.orgs;

import com.alorma.github.sdk.bean.dto.response.ListOrganizations;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 04/09/2014.
 */
public interface OrgsService {

	@GET("/user/orgs")
	void orgs(Callback<ListOrganizations> callback);

	@GET("/users/{username}/orgs")
	void orgsByUser(@Path("username") String username, Callback<ListOrganizations> callback);

	@GET("/user/orgs")
	void orgs(@Query("page") int page, Callback<ListOrganizations> callback);

	@GET("/users/{username}/orgs")
	void orgsByUser(@Path("username") String username, @Query("page") int page, Callback<ListOrganizations> callback);

}
