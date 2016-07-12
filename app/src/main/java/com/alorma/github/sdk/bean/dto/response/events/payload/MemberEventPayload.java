package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.User;

public class MemberEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<MemberEventPayload> CREATOR = new Creator<MemberEventPayload>() {
    public MemberEventPayload createFromParcel(Parcel source) {
      return new MemberEventPayload(source);
    }

    public MemberEventPayload[] newArray(int size) {
      return new MemberEventPayload[size];
    }
  };
  public User member;

  public MemberEventPayload() {
  }

  protected MemberEventPayload(Parcel in) {
    super(in);
    this.member = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.member, 0);
  }
}
