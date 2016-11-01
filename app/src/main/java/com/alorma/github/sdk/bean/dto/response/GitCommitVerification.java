package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitCommitVerification implements Parcelable {
  public boolean verified;
  public String reason;
  public String signature;
  public String payload;

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.verified ? (byte) 1 : (byte) 0);
    dest.writeString(this.reason);
    dest.writeString(this.signature);
    dest.writeString(this.payload);
  }

  public GitCommitVerification() {
  }

  protected GitCommitVerification(Parcel in) {
    this.verified = in.readByte() != 0;
    this.reason = in.readString();
    this.signature = in.readString();
    this.payload = in.readString();
  }

  public static final Parcelable.Creator<GitCommitVerification> CREATOR = new Parcelable.Creator<GitCommitVerification>() {
    @Override
    public GitCommitVerification createFromParcel(Parcel source) {
      return new GitCommitVerification(source);
    }

    @Override
    public GitCommitVerification[] newArray(int size) {
      return new GitCommitVerification[size];
    }
  };
}
