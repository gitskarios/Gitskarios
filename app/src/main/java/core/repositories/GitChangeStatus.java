package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;

public class GitChangeStatus implements Parcelable {

  public int additions;
  public int deletions;
  public int total;
  public int changes;

  public GitChangeStatus() {
  }

  public int getAdditions() {
    return additions;
  }

  public void setAdditions(int additions) {
    this.additions = additions;
  }

  public int getDeletions() {
    return deletions;
  }

  public void setDeletions(int deletions) {
    this.deletions = deletions;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getChanges() {
    return changes;
  }

  public void setChanges(int changes) {
    this.changes = changes;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.additions);
    dest.writeInt(this.deletions);
    dest.writeInt(this.total);
    dest.writeInt(this.changes);
  }

  protected GitChangeStatus(Parcel in) {
    this.additions = in.readInt();
    this.deletions = in.readInt();
    this.total = in.readInt();
    this.changes = in.readInt();
  }

  public static final Creator<GitChangeStatus> CREATOR = new Creator<GitChangeStatus>() {
    @Override
    public GitChangeStatus createFromParcel(Parcel source) {
      return new GitChangeStatus(source);
    }

    @Override
    public GitChangeStatus[] newArray(int size) {
      return new GitChangeStatus[size];
    }
  };
}