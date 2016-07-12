package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.GollumPage;
import java.util.List;

public class GollumEventPayload extends GithubEventPayload implements Parcelable {

  public static final Creator<GollumEventPayload> CREATOR = new Creator<GollumEventPayload>() {
    public GollumEventPayload createFromParcel(Parcel source) {
      return new GollumEventPayload(source);
    }

    public GollumEventPayload[] newArray(int size) {
      return new GollumEventPayload[size];
    }
  };
  public List<GollumPage> pages;

  public GollumEventPayload() {
  }

  protected GollumEventPayload(Parcel in) {
    super(in);
    this.pages = in.createTypedArrayList(GollumPage.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeTypedList(pages);
  }
}
