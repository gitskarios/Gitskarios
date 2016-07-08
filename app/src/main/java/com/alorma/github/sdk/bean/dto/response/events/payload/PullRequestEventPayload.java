package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 05/10/2014.
 */
public class PullRequestEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<PullRequestEventPayload> CREATOR =
      new Creator<PullRequestEventPayload>() {
        public PullRequestEventPayload createFromParcel(Parcel source) {
          return new PullRequestEventPayload(source);
        }

        public PullRequestEventPayload[] newArray(int size) {
          return new PullRequestEventPayload[size];
        }
      };
  public int number;
  public PullRequest pull_request;
  @SerializedName("public") public boolean is_public;
  public Organization org;
  public String created_at;

  public PullRequestEventPayload() {
  }

  protected PullRequestEventPayload(Parcel in) {
    super(in);
    this.number = in.readInt();
    this.pull_request = in.readParcelable(PullRequest.class.getClassLoader());
    this.is_public = in.readByte() != 0;
    this.org = in.readParcelable(Organization.class.getClassLoader());
    this.created_at = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.number);
    dest.writeParcelable(this.pull_request, 0);
    dest.writeByte(is_public ? (byte) 1 : (byte) 0);
    dest.writeParcelable(this.org, 0);
    dest.writeString(this.created_at);
  }
}
