package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 13/07/2014.
 */
public class RequestTokenDTO implements Parcelable {
  public static final Parcelable.Creator<RequestTokenDTO> CREATOR =
      new Parcelable.Creator<RequestTokenDTO>() {
        public RequestTokenDTO createFromParcel(Parcel source) {
          return new RequestTokenDTO(source);
        }

        public RequestTokenDTO[] newArray(int size) {
          return new RequestTokenDTO[size];
        }
      };
  public String client_id;
  public String client_secret;
  public String code;
  public String redirect_uri;

  public RequestTokenDTO() {
  }

  protected RequestTokenDTO(Parcel in) {
    this.client_id = in.readString();
    this.client_secret = in.readString();
    this.code = in.readString();
    this.redirect_uri = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.client_id);
    dest.writeString(this.client_secret);
    dest.writeString(this.code);
    dest.writeString(this.redirect_uri);
  }
}
