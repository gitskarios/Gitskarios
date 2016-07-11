package com.alorma.github.sdk.bean.dto.response.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.User;
import java.util.List;

/**
 * Created by Bernat on 08/08/2014.
 */
public class UsersSearch extends SearchBase implements Parcelable {
  public static final Creator<UsersSearch> CREATOR = new Creator<UsersSearch>() {
    public UsersSearch createFromParcel(Parcel source) {
      return new UsersSearch(source);
    }

    public UsersSearch[] newArray(int size) {
      return new UsersSearch[size];
    }
  };
  public List<User> items;

  public UsersSearch() {
  }

  protected UsersSearch(Parcel in) {
    super(in);
    this.items = in.createTypedArrayList(User.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeTypedList(items);
  }
}
