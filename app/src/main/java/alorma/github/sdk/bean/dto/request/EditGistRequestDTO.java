package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.GistFilesMap;

public class EditGistRequestDTO implements Parcelable {

  public static final Creator<EditGistRequestDTO> CREATOR = new Creator<EditGistRequestDTO>() {
    public EditGistRequestDTO createFromParcel(Parcel source) {
      return new EditGistRequestDTO(source);
    }

    public EditGistRequestDTO[] newArray(int size) {
      return new EditGistRequestDTO[size];
    }
  };
  public String description;
  public GistFilesMap files;

  public EditGistRequestDTO() {
  }

  protected EditGistRequestDTO(Parcel in) {
    this.description = in.readString();
    this.files = in.readParcelable(GistFilesMap.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.description);
    dest.writeParcelable(this.files, 0);
  }
}
