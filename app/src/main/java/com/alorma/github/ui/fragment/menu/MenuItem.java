package com.alorma.github.ui.fragment.menu;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItem implements Parcelable {
	public int parentId;
	public int text;
	public int id;
	public GithubIconify.IconValue icon;
	public int color = -1;

	public MenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon) {
		this.text = text;
		this.id = id;
		this.parentId = parentId;
		this.icon = icon;
	}

	public MenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon, int color) {
		this(id, parentId, text, icon);
		this.color = color;
	}

	protected MenuItem(Parcel in) {
		parentId = in.readInt();
		text = in.readInt();
		id = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(parentId);
		dest.writeInt(text);
		dest.writeInt(id);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>() {
		@Override
		public MenuItem createFromParcel(Parcel in) {
			return new MenuItem(in);
		}

		@Override
		public MenuItem[] newArray(int size) {
			return new MenuItem[size];
		}
	};
}