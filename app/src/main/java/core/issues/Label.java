package core.issues;

import android.os.Parcel;
import android.os.Parcelable;
import core.ShaUrl;

public class Label extends ShaUrl implements Comparable<Label>,Parcelable {

  public String name;
  public String color;

  public Label() {
  }

  @Override
  public int compareTo(Label another) {
    return name.toLowerCase().compareTo(another.name.toLowerCase());
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.color);
  }

  protected Label(Parcel in) {
    this.name = in.readString();
    this.color = in.readString();
  }

  public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {
    @Override
    public Label createFromParcel(Parcel source) {
      return new Label(source);
    }

    @Override
    public Label[] newArray(int size) {
      return new Label[size];
    }
  };
}
