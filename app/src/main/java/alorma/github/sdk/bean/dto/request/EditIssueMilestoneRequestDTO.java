package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 15/04/2015.
 */
public class EditIssueMilestoneRequestDTO extends EditIssueRequestDTO implements Parcelable {
  public static final Parcelable.Creator<EditIssueMilestoneRequestDTO> CREATOR =
      new Parcelable.Creator<EditIssueMilestoneRequestDTO>() {
        public EditIssueMilestoneRequestDTO createFromParcel(Parcel source) {
          return new EditIssueMilestoneRequestDTO(source);
        }

        public EditIssueMilestoneRequestDTO[] newArray(int size) {
          return new EditIssueMilestoneRequestDTO[size];
        }
      };
  public Integer milestone;

  public EditIssueMilestoneRequestDTO() {
  }

  protected EditIssueMilestoneRequestDTO(Parcel in) {
    this.milestone = (Integer) in.readValue(Integer.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.milestone);
  }
}
