package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Repo;

/**
 * Created by Bernat on 05/10/2014.
 */
public class ForkEventPayload extends GithubEventPayload implements Parcelable {
  public static final Parcelable.Creator<ForkEventPayload> CREATOR =
      new Parcelable.Creator<ForkEventPayload>() {
        public ForkEventPayload createFromParcel(Parcel source) {
          return new ForkEventPayload(source);
        }

        public ForkEventPayload[] newArray(int size) {
          return new ForkEventPayload[size];
        }
      };
  public Repo forkee;
  public Repo repository;

  public ForkEventPayload() {
  }

  protected ForkEventPayload(Parcel in) {
    this.forkee = in.readParcelable(Repo.class.getClassLoader());
    this.repository = in.readParcelable(Repo.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.forkee, 0);
    dest.writeParcelable(this.repository, 0);
  }
}
