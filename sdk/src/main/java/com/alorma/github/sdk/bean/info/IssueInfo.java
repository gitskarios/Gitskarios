package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 06/09/2014.
 */
public class IssueInfo implements Parcelable{

	public String owner;
	public String repo;
	public int num;

	public IssueInfo() {
	}

	public IssueInfo(Parcel in) {
		owner = in.readString();
		repo = in.readString();
		num = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(owner);
		dest.writeString(repo);
		dest.writeInt(num);
	}



	public static final Parcelable.Creator<IssueInfo> CREATOR = new Parcelable.Creator<IssueInfo>() {
		@Override
		public IssueInfo createFromParcel(Parcel in) {
			return new IssueInfo(in);
		}

		@Override
		public IssueInfo[] newArray(int size) {
			return new IssueInfo[size];
		}
	};
}
