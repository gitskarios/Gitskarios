package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.IssueState;

public class IssueInfo implements Parcelable {

  public static final Creator<IssueInfo> CREATOR = new Creator<IssueInfo>() {
    public IssueInfo createFromParcel(Parcel source) {
      return new IssueInfo(source);
    }

    public IssueInfo[] newArray(int size) {
      return new IssueInfo[size];
    }
  };
  public RepoInfo repoInfo;
  public int num;
  public int commentNum;
  public IssueState state = IssueState.open;

  public IssueInfo() {
  }

  public IssueInfo(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  protected IssueInfo(Parcel in) {
    this.repoInfo = in.readParcelable(RepoInfo.class.getClassLoader());
    this.num = in.readInt();
    this.commentNum = in.readInt();
    int tmpState = in.readInt();
    this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
  }

  @Override
  public String toString() {
    return repoInfo.toString() + "#" + num;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.repoInfo, 0);
    dest.writeInt(this.num);
    dest.writeInt(this.commentNum);
    dest.writeInt(this.state == null ? -1 : this.state.ordinal());
  }
}
