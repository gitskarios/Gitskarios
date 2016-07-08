package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 21/06/2015.
 */
public class MergeButtonRequest implements Parcelable {
  public static final Parcelable.Creator<MergeButtonRequest> CREATOR =
      new Parcelable.Creator<MergeButtonRequest>() {
        public MergeButtonRequest createFromParcel(Parcel source) {
          return new MergeButtonRequest(source);
        }

        public MergeButtonRequest[] newArray(int size) {
          return new MergeButtonRequest[size];
        }
      };
  public String commit_message;
  public String sha;

  public MergeButtonRequest() {
  }

  protected MergeButtonRequest(Parcel in) {
    this.commit_message = in.readString();
    this.sha = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.commit_message);
    dest.writeString(this.sha);
  }
}
