package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;

public class Links implements Parcelable {
  public String html;
  public String self;
  public String git;

  public Links() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.html);
    dest.writeString(this.self);
    dest.writeString(this.git);
  }

  protected Links(Parcel in) {
    this.html = in.readString();
    this.self = in.readString();
    this.git = in.readString();
  }

  public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
    @Override
    public Links createFromParcel(Parcel source) {
      return new Links(source);
    }

    @Override
    public Links[] newArray(int size) {
      return new Links[size];
    }
  };
}
