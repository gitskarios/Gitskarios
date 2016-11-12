package core.repositories;

import android.os.Parcel;
import com.alorma.github.sdk.bean.dto.response.GitCommitVerification;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import core.ShaUrl;
import core.User;
import java.util.ArrayList;
import java.util.List;

public class Commit extends ShaUrl {

  private static final int MAX_COMMIT_LENGHT = 80;

  public GitCommit commit;
  public User author;
  public List<ShaUrl> parents;
  public GitChangeStatus stats;
  public User committer;
  public String message;
  public boolean distinct;
  public GitCommitFiles files;
  public int days;
  public int comment_count;
  private GithubStatusResponse combinedStatus;

  public Commit() {
  }

  public GitCommit getCommit() {
    return commit;
  }

  public void setCommit(GitCommit commit) {
    this.commit = commit;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public List<ShaUrl> getParents() {
    return parents;
  }

  public void setParents(List<ShaUrl> parents) {
    this.parents = parents;
  }

  public GitChangeStatus getStats() {
    return stats;
  }

  public void setStats(GitChangeStatus stats) {
    this.stats = stats;
  }

  public User getCommitter() {
    return committer;
  }

  public void setCommitter(User committer) {
    this.committer = committer;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isDistinct() {
    return distinct;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public GitCommitFiles getFiles() {
    return files;
  }

  public void setFiles(GitCommitFiles files) {
    this.files = files;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public int getComment_count() {
    return comment_count;
  }

  public void setComment_count(int comment_count) {
    this.comment_count = comment_count;
  }

  public void setCombinedStatus(GithubStatusResponse combinedStatus) {
    this.combinedStatus = combinedStatus;
  }

  public GithubStatusResponse getCombinedStatus() {
    return combinedStatus;
  }

  public String shortMessage() {
    if (message != null) {
      int start = 0;
      int end = Math.min(MAX_COMMIT_LENGHT, message.length());

      return message.substring(start, end);
    }
    return null;
  }

  public boolean isCommitVerified() {
    if (commit != null) {
      GitCommitVerification verification = commit.verification;
      return verification != null && verification.verified;
    }
    return false;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.commit, flags);
    dest.writeParcelable(this.author, flags);
    dest.writeTypedList(this.parents);
    dest.writeParcelable(this.stats, flags);
    dest.writeParcelable(this.committer, flags);
    dest.writeString(this.message);
    dest.writeByte(this.distinct ? (byte) 1 : (byte) 0);
    dest.writeInt(this.days);
    dest.writeInt(this.comment_count);
    dest.writeParcelable(this.combinedStatus, flags);
    dest.writeString(this.htmlUrl);
    dest.writeString(this.sha);
    dest.writeString(this.url);
  }

  protected Commit(Parcel in) {
    super(in);
    this.commit = in.readParcelable(GitCommit.class.getClassLoader());
    this.author = in.readParcelable(User.class.getClassLoader());
    this.parents = in.createTypedArrayList(ShaUrl.CREATOR);
    this.stats = in.readParcelable(GitChangeStatus.class.getClassLoader());
    this.committer = in.readParcelable(User.class.getClassLoader());
    this.message = in.readString();
    this.distinct = in.readByte() != 0;
    this.days = in.readInt();
    this.comment_count = in.readInt();
    this.combinedStatus = in.readParcelable(GithubStatusResponse.class.getClassLoader());
    this.htmlUrl = in.readString();
    this.sha = in.readString();
    this.url = in.readString();
  }

  public static final Creator<Commit> CREATOR = new Creator<Commit>() {
    @Override
    public Commit createFromParcel(Parcel source) {
      return new Commit(source);
    }

    @Override
    public Commit[] newArray(int size) {
      return new Commit[size];
    }
  };
}