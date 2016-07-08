package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Contributor implements Parcelable {
  public static final Parcelable.Creator<Contributor> CREATOR =
      new Parcelable.Creator<Contributor>() {
        public Contributor createFromParcel(Parcel source) {
          return new Contributor(source);
        }

        public Contributor[] newArray(int size) {
          return new Contributor[size];
        }
      };
  public User author;
  public int id;
  public int total;
  public List<Week> weeks;

  public Contributor() {
  }

  protected Contributor(Parcel in) {
    this.author = in.readParcelable(User.class.getClassLoader());
    this.id = in.readInt();
    this.total = in.readInt();
    this.weeks = new ArrayList<Week>();
    in.readList(this.weeks, List.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.author, 0);
    dest.writeInt(this.id);
    dest.writeInt(this.total);
    dest.writeList(this.weeks);
  }
}
