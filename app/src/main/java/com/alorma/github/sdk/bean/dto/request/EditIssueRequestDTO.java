package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 15/04/2015.
 */
public abstract class EditIssueRequestDTO implements Parcelable {

  public EditIssueRequestDTO() {
  }

  protected EditIssueRequestDTO(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}
