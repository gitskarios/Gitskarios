package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class EditIssueAssigneeRequestDTO extends EditIssueRequestDTO implements Parcelable {
  public static final Parcelable.Creator<EditIssueAssigneeRequestDTO> CREATOR =
      new Parcelable.Creator<EditIssueAssigneeRequestDTO>() {
        public EditIssueAssigneeRequestDTO createFromParcel(Parcel source) {
          return new EditIssueAssigneeRequestDTO(source);
        }

        public EditIssueAssigneeRequestDTO[] newArray(int size) {
          return new EditIssueAssigneeRequestDTO[size];
        }
      };
  public String assignee;

  public EditIssueAssigneeRequestDTO() {
  }

  protected EditIssueAssigneeRequestDTO(Parcel in) {
    this.assignee = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.assignee);
  }
}
