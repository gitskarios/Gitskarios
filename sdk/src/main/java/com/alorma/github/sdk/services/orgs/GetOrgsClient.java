package com.alorma.github.sdk.services.orgs;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 04/09/2014.
 */
public class GetOrgsClient extends BaseClient<ListOrganizations> {
	private String username;
	private int page = -1;

	public GetOrgsClient(Context context, String username) {
		super(context);
		this.username = username;
	}

	public GetOrgsClient(Context context, String org, int page) {
		super(context);
		this.username = org;
		this.page = page;
	}

	@Override
	protected void executeService(RestAdapter restAdapter) {
		OrgsService orgsService = restAdapter.create(OrgsService.class);
		if (page == -1) {
			if (username == null) {
				orgsService.orgs(this);
			} else {
				orgsService.orgsByUser(username, this);
			}
		} else {
			if (username == null) {
				orgsService.orgs(page, this);
			} else {
				orgsService.orgsByUser(username, page, this);
			}
		}
	}
}
