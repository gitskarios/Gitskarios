package com.alorma.github.sdk.bean.info;

import android.os.Parcel;

/**
 * Created by Bernat on 07/09/2014.
 */
public class RepoInfo extends OwnerInfo {

	public String repo;

	public RepoInfo() {
	}

	public RepoInfo(Parcel in) {
		super(in);

		repo = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(repo);
	}
}
