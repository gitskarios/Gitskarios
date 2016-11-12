package core;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class ShaUrl extends Sha implements Parcelable {

  @SerializedName("html_url")
  public String htmlUrl;

  public ShaUrl() {
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  public void setHtmlUrl(String htmlUrl) {
    this.htmlUrl = htmlUrl;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.htmlUrl);
  }

  protected ShaUrl(Parcel in) {
    super(in);
    this.htmlUrl = in.readString();
  }

  public static final Creator<ShaUrl> CREATOR = new Creator<ShaUrl>() {
    @Override
    public ShaUrl createFromParcel(Parcel source) {
      return new ShaUrl(source);
    }

    @Override
    public ShaUrl[] newArray(int size) {
      return new ShaUrl[size];
    }
  };
}
