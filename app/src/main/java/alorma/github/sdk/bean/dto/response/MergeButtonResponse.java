package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 21/06/2015.
 */
public class MergeButtonResponse extends ShaUrl implements Parcelable {

  public static final Creator<MergeButtonResponse> CREATOR = new Creator<MergeButtonResponse>() {
    public MergeButtonResponse createFromParcel(Parcel source) {
      return new MergeButtonResponse(source);
    }

    public MergeButtonResponse[] newArray(int size) {
      return new MergeButtonResponse[size];
    }
  };
  public Boolean merged;
  public String message;

  public MergeButtonResponse() {
  }

  protected MergeButtonResponse(Parcel in) {
    super(in);
    this.merged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.message = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeValue(this.merged);
    dest.writeString(this.message);
  }
}
