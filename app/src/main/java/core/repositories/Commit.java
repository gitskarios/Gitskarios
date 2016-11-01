package core.repositories;

import com.alorma.github.sdk.bean.dto.response.GitCommitVerification;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import core.ShaUrl;
import core.User;
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
}