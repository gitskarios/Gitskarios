package com.alorma.github.sdk.bean.dto.response.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Repo;
import java.util.List;

/**
 * Created by Bernat on 08/08/2014.
 */
public class ReposSearch extends SearchBase implements Parcelable {
  public static final Parcelable.Creator<ReposSearch> CREATOR =
      new Parcelable.Creator<ReposSearch>() {
        public ReposSearch createFromParcel(Parcel source) {
          return new ReposSearch(source);
        }

        public ReposSearch[] newArray(int size) {
          return new ReposSearch[size];
        }
      };
  public List<Repo> items;

  public ReposSearch() {
  }

  protected ReposSearch(Parcel in) {
    this.items = in.createTypedArrayList(Repo.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(items);
  }
}
