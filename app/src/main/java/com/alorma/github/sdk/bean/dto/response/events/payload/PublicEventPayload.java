package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;

public class PublicEventPayload extends GithubEventPayload implements Parcelable {

  public static final Creator<PublicEventPayload> CREATOR = new Creator<PublicEventPayload>() {
    public PublicEventPayload createFromParcel(Parcel source) {
      return new PublicEventPayload(source);
    }

    public PublicEventPayload[] newArray(int size) {
      return new PublicEventPayload[size];
    }
  };
  public Repo repository;
  public User sender;

  public PublicEventPayload() {
  }

  protected PublicEventPayload(Parcel in) {
    super(in);
    this.repository = in.readParcelable(Repo.class.getClassLoader());
    this.sender = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.repository, 0);
    dest.writeParcelable(this.sender, 0);
  }
}
