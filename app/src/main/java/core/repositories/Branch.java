package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;

public class Branch implements Parcelable {

  public String name;
  public Commit commit;
  public Links _links;

  public Branch() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Commit getCommit() {
    return commit;
  }

  public void setCommit(Commit commit) {
    this.commit = commit;
  }

  public Links get_links() {
    return _links;
  }

  public void set_links(Links _links) {
    this._links = _links;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeParcelable(this.commit, flags);
    dest.writeParcelable(this._links, flags);
  }

  protected Branch(Parcel in) {
    this.name = in.readString();
    this.commit = in.readParcelable(Commit.class.getClassLoader());
    this._links = in.readParcelable(Links.class.getClassLoader());
  }

  public static final Creator<Branch> CREATOR = new Creator<Branch>() {
    @Override
    public Branch createFromParcel(Parcel source) {
      return new Branch(source);
    }

    @Override
    public Branch[] newArray(int size) {
      return new Branch[size];
    }
  };
}
