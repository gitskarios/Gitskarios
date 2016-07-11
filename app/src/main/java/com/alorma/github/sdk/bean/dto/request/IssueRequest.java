package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.IssueState;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueRequest extends EditIssueRequestDTO implements Parcelable {
  public static final Creator<IssueRequest> CREATOR = new Creator<IssueRequest>() {
    public IssueRequest createFromParcel(Parcel source) {
      return new IssueRequest(source);
    }

    public IssueRequest[] newArray(int size) {
      return new IssueRequest[size];
    }
  };
  public String title;
  public String body;
  public String assignee;
  public Integer milestone;
  public String milestoneName;
  public String[] labels;
  public IssueState state;

  public IssueRequest() {
  }

  protected IssueRequest(Parcel in) {
    super(in);
    this.title = in.readString();
    this.body = in.readString();
    this.assignee = in.readString();
    this.milestone = (Integer) in.readValue(Integer.class.getClassLoader());
    this.milestoneName = in.readString();
    this.labels = in.createStringArray();
    int tmpState = in.readInt();
    this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.title);
    dest.writeString(this.body);
    dest.writeString(this.assignee);
    dest.writeValue(this.milestone);
    dest.writeString(this.milestoneName);
    dest.writeStringArray(this.labels);
    dest.writeInt(this.state == null ? -1 : this.state.ordinal());
  }
}
