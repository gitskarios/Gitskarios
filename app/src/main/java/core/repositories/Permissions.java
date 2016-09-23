package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;

public class Permissions implements Parcelable {

  public boolean admin;
  public boolean push;
  public boolean pull;

  public Permissions() {
    this.admin = false;
    this.push = false;
    this.pull = false;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
    dest.writeByte(this.push ? (byte) 1 : (byte) 0);
    dest.writeByte(this.pull ? (byte) 1 : (byte) 0);
  }

  protected Permissions(Parcel in) {
    this.admin = in.readByte() != 0;
    this.push = in.readByte() != 0;
    this.pull = in.readByte() != 0;
  }

  public static final Parcelable.Creator<Permissions> CREATOR = new Parcelable.Creator<Permissions>() {
    @Override
    public Permissions createFromParcel(Parcel source) {
      return new Permissions(source);
    }

    @Override
    public Permissions[] newArray(int size) {
      return new Permissions[size];
    }
  };
}
