package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class EditIssueTitleRequestDTO extends EditIssueRequestDTO implements Parcelable {
  public static final Creator<EditIssueTitleRequestDTO> CREATOR =
      new Creator<EditIssueTitleRequestDTO>() {
        public EditIssueTitleRequestDTO createFromParcel(Parcel source) {
          return new EditIssueTitleRequestDTO(source);
        }

        public EditIssueTitleRequestDTO[] newArray(int size) {
          return new EditIssueTitleRequestDTO[size];
        }
      };
  public String title;

  public EditIssueTitleRequestDTO() {
  }

  protected EditIssueTitleRequestDTO(Parcel in) {
    super(in);
    this.title = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.title);
  }
}
