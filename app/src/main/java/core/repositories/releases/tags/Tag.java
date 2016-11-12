package core.repositories.releases.tags;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import core.Exclude;
import core.Sha;
import core.repositories.Commit;
import core.repositories.releases.Release;

public class Tag implements Parcelable {
  @SerializedName("name") private String name;
  @SerializedName("commit") private Sha sha;
  @SerializedName("zipball_url") private String zipballUrl;
  @SerializedName("tarball_url") private String tarballUrl;

  @Exclude public Commit commit;
  @Exclude public Release release;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Sha getSha() {
    return sha;
  }

  public void setSha(Sha sha) {
    this.sha = sha;
  }

  public String getZipballUrl() {
    return zipballUrl;
  }

  public void setZipballUrl(String zipballUrl) {
    this.zipballUrl = zipballUrl;
  }

  public String getTarballUrl() {
    return tarballUrl;
  }

  public void setTarballUrl(String tarballUrl) {
    this.tarballUrl = tarballUrl;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeParcelable(this.sha, flags);
    dest.writeString(this.zipballUrl);
    dest.writeString(this.tarballUrl);
    dest.writeParcelable(this.commit, flags);
    dest.writeParcelable(this.release, flags);
  }

  public Tag() {
  }

  protected Tag(Parcel in) {
    this.name = in.readString();
    this.sha = in.readParcelable(Sha.class.getClassLoader());
    this.zipballUrl = in.readString();
    this.tarballUrl = in.readString();
    this.commit = in.readParcelable(Commit.class.getClassLoader());
    this.release = in.readParcelable(Release.class.getClassLoader());
  }

  public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
    @Override
    public Tag createFromParcel(Parcel source) {
      return new Tag(source);
    }

    @Override
    public Tag[] newArray(int size) {
      return new Tag[size];
    }
  };
}
