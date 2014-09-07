package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 06/09/2014.
 */
public class IssueInfo extends RepoInfo implements Parcelable{
	public int num;

	public String repo;

	public IssueInfo() {
	}

	public IssueInfo(Parcel in) {
		super(in);

		num = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(num);
	}

}
