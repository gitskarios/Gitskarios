package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 05/10/2014.
 */
public class CreatedEventPayload extends DeleteEventPayload implements Parcelable {
  public static final Parcelable.Creator<CreatedEventPayload> CREATOR =
      new Parcelable.Creator<CreatedEventPayload>() {
        public CreatedEventPayload createFromParcel(Parcel source) {
          return new CreatedEventPayload(source);
        }

        public CreatedEventPayload[] newArray(int size) {
          return new CreatedEventPayload[size];
        }
      };
  public String master_branch;
  public String description;

  public CreatedEventPayload() {
  }

  protected CreatedEventPayload(Parcel in) {
    this.master_branch = in.readString();
    this.description = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.master_branch);
    dest.writeString(this.description);
  }
}
