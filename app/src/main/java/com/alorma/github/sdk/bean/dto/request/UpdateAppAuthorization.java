package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by Bernat on 19/02/2015.
 */
public class UpdateAppAuthorization implements Parcelable {
  public static final Parcelable.Creator<UpdateAppAuthorization> CREATOR =
      new Parcelable.Creator<UpdateAppAuthorization>() {
        public UpdateAppAuthorization createFromParcel(Parcel source) {
          return new UpdateAppAuthorization(source);
        }

        public UpdateAppAuthorization[] newArray(int size) {
          return new UpdateAppAuthorization[size];
        }
      };
  public String client_secret;
  public List<String> scopes;
  public String note;

  public UpdateAppAuthorization() {
  }

  protected UpdateAppAuthorization(Parcel in) {
    this.client_secret = in.readString();
    this.scopes = in.createStringArrayList();
    this.note = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.client_secret);
    dest.writeStringList(this.scopes);
    dest.writeString(this.note);
  }
}
