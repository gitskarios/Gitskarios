package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 13/07/2014.
 */
public class Permissions implements Parcelable {
	public boolean admin;
	public boolean push;
	public boolean pull;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Permissions{");
		sb.append("admin=").append(admin);
		sb.append(", push=").append(push);
		sb.append(", pull=").append(pull);
		sb.append('}');
		return sb.toString();
	}

	protected Permissions(Parcel in) {
		admin = in.readByte() != 0x00;
		push = in.readByte() != 0x00;
		pull = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (admin ? 0x01 : 0x00));
		dest.writeByte((byte) (push ? 0x01 : 0x00));
		dest.writeByte((byte) (pull ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Permissions> CREATOR = new Parcelable.Creator<Permissions>() {
		@Override
		public Permissions createFromParcel(Parcel in) {
			return new Permissions(in);
		}

		@Override
		public Permissions[] newArray(int size) {
			return new Permissions[size];
		}
	};
}
