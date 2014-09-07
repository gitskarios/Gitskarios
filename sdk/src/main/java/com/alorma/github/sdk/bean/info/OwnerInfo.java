package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 07/09/2014.
 */
public class OwnerInfo implements Parcelable {

	public String owner;

	public OwnerInfo() {

	}

	public OwnerInfo(Parcel in) {
		owner = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(owner);
	}

	public static final Parcelable.Creator<OwnerInfo> CREATOR = new Parcelable.Creator<OwnerInfo>() {
		@Override
		public OwnerInfo createFromParcel(Parcel in) {
			return new OwnerInfo(in);
		}

		@Override
		public OwnerInfo[] newArray(int size) {
			return new OwnerInfo[size];
		}
	};
}
