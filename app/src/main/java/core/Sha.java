package core;

import android.os.Parcel;
import android.os.Parcelable;

public class Sha implements Parcelable {
  private static final int MAX_SHA_LENGHT = 8;

  public String sha;
  public String url;

  public Sha() {
  }

  public String getSha() {
    return sha;
  }

  public void setSha(String sha) {
    this.sha = sha;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String shortSha() {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.sha);
    dest.writeString(this.url);
  }

  protected Sha(Parcel in) {
    this.sha = in.readString();
    this.url = in.readString();
  }

  public static final Creator<Sha> CREATOR = new Creator<Sha>() {
    @Override
    public Sha createFromParcel(Parcel source) {
      return new Sha(source);
    }

    @Override
    public Sha[] newArray(int size) {
      return new Sha[size];
    }
  };
}
