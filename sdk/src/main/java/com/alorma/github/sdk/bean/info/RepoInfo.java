package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 07/09/2014.
 */
public class RepoInfo implements Parcelable{

	public String owner;
	public String repo;

	public RepoInfo() {
	}

	public RepoInfo(Parcel in) {
		owner = in.readString();
		repo = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(owner);
		dest.writeString(repo);
	}


	public static final Parcelable.Creator<RepoInfo> CREATOR = new Parcelable.Creator<RepoInfo>() {
		@Override
		public RepoInfo createFromParcel(Parcel in) {
			return new RepoInfo(in);
		}

		@Override
		public RepoInfo[] newArray(int size) {
			return new RepoInfo[size];
		}
	};
}
