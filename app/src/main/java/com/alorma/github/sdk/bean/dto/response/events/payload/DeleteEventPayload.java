package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.UserType;

/**
 * Created by Bernat on 05/10/2014.
 */
public class DeleteEventPayload extends GithubEventPayload implements Parcelable {
  public static final Parcelable.Creator<DeleteEventPayload> CREATOR =
      new Parcelable.Creator<DeleteEventPayload>() {
        public DeleteEventPayload createFromParcel(Parcel source) {
          return new DeleteEventPayload(source);
        }

        public DeleteEventPayload[] newArray(int size) {
          return new DeleteEventPayload[size];
        }
      };
  public String ref;
  public String ref_type;
  public UserType pusher_type;

  public DeleteEventPayload() {
  }

  protected DeleteEventPayload(Parcel in) {
    this.ref = in.readString();
    this.ref_type = in.readString();
    int tmpPusher_type = in.readInt();
    this.pusher_type = tmpPusher_type == -1 ? null : UserType.values()[tmpPusher_type];
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.ref);
    dest.writeString(this.ref_type);
    dest.writeInt(this.pusher_type == null ? -1 : this.pusher_type.ordinal());
  }
}
