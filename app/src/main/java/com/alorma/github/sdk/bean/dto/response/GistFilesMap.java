package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;

/**
 * Created by bernat.borras on 9/1/16.
 */
public class GistFilesMap extends HashMap<String, GistFile> implements Parcelable {
  public static final Parcelable.Creator<GistFilesMap> CREATOR =
      new Parcelable.Creator<GistFilesMap>() {
        public GistFilesMap createFromParcel(Parcel source) {
          return new GistFilesMap(source);
        }

        public GistFilesMap[] newArray(int size) {
          return new GistFilesMap[size];
        }
      };

  public GistFilesMap() {
  }

  protected GistFilesMap(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}
