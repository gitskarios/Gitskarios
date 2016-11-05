package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class License implements Parcelable {
  public String key;
  public String name;
  public String url;
  @SerializedName("spdx_id")
  public String spdxId;
  public boolean featured;

  public License() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.key);
    dest.writeString(this.name);
    dest.writeString(this.url);
    dest.writeString(this.spdxId);
    dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
  }

  protected License(Parcel in) {
    this.key = in.readString();
    this.name = in.readString();
    this.url = in.readString();
    this.spdxId = in.readString();
    this.featured = in.readByte() != 0;
  }

  public static final Parcelable.Creator<License> CREATOR = new Parcelable.Creator<License>() {
    @Override
    public License createFromParcel(Parcel source) {
      return new License(source);
    }

    @Override
    public License[] newArray(int size) {
      return new License[size];
    }
  };
}
