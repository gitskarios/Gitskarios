package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by Bernat on 28/09/2014.
 */
public class GitIgnoreTemplates extends ArrayList<String> implements Parcelable {
  public static final Parcelable.Creator<GitIgnoreTemplates> CREATOR =
      new Parcelable.Creator<GitIgnoreTemplates>() {
        public GitIgnoreTemplates createFromParcel(Parcel source) {
          return new GitIgnoreTemplates(source);
        }

        public GitIgnoreTemplates[] newArray(int size) {
          return new GitIgnoreTemplates[size];
        }
      };

  public GitIgnoreTemplates() {
  }

  protected GitIgnoreTemplates(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}
