package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

public class EditIssueClearAssigneesRequestDTO extends EditIssueRequestDTO implements Parcelable {
  public String assignee;

  public EditIssueClearAssigneesRequestDTO() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.assignee);
  }

  protected EditIssueClearAssigneesRequestDTO(Parcel in) {
    super(in);
    this.assignee = in.readString();
  }

  public static final Creator<EditIssueClearAssigneesRequestDTO> CREATOR = new Creator<EditIssueClearAssigneesRequestDTO>() {
    @Override
    public EditIssueClearAssigneesRequestDTO createFromParcel(Parcel source) {
      return new EditIssueClearAssigneesRequestDTO(source);
    }

    @Override
    public EditIssueClearAssigneesRequestDTO[] newArray(int size) {
      return new EditIssueClearAssigneesRequestDTO[size];
    }
  };
}
