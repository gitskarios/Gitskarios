package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 22/07/2014.
 */
public class RequestMarkdownDTO implements Parcelable {
  public static final Parcelable.Creator<RequestMarkdownDTO> CREATOR =
      new Parcelable.Creator<RequestMarkdownDTO>() {
        public RequestMarkdownDTO createFromParcel(Parcel source) {
          return new RequestMarkdownDTO(source);
        }

        public RequestMarkdownDTO[] newArray(int size) {
          return new RequestMarkdownDTO[size];
        }
      };
  public String text;

  public RequestMarkdownDTO() {
  }

  protected RequestMarkdownDTO(Parcel in) {
    this.text = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.text);
  }
}
