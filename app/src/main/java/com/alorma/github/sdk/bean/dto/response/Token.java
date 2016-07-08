package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 13/07/2014.
 */
public class Token implements Parcelable {
  public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
    public Token createFromParcel(Parcel source) {
      return new Token(source);
    }

    public Token[] newArray(int size) {
      return new Token[size];
    }
  };
  public String access_token;
  public String token_type;
  public String scope;
  public String error;
  public String error_description;
  public String error_uri;

  public Token() {
  }

  protected Token(Parcel in) {
    this.access_token = in.readString();
    this.token_type = in.readString();
    this.scope = in.readString();
    this.error = in.readString();
    this.error_description = in.readString();
    this.error_uri = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.access_token);
    dest.writeString(this.token_type);
    dest.writeString(this.scope);
    dest.writeString(this.error);
    dest.writeString(this.error_description);
    dest.writeString(this.error_uri);
  }
}
