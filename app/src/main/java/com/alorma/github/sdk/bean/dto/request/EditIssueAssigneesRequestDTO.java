package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class EditIssueAssigneesRequestDTO extends EditIssueRequestDTO implements Parcelable {
  public List<String> assignees;

  public EditIssueAssigneesRequestDTO() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeStringList(this.assignees);
  }

  protected EditIssueAssigneesRequestDTO(Parcel in) {
    super(in);
    this.assignees = in.createStringArrayList();
  }

  public static final Creator<EditIssueAssigneesRequestDTO> CREATOR = new Creator<EditIssueAssigneesRequestDTO>() {
    @Override
    public EditIssueAssigneesRequestDTO createFromParcel(Parcel source) {
      return new EditIssueAssigneesRequestDTO(source);
    }

    @Override
    public EditIssueAssigneesRequestDTO[] newArray(int size) {
      return new EditIssueAssigneesRequestDTO[size];
    }
  };
}
