package core.repositories;

import com.alorma.github.sdk.bean.dto.response.GitCommitVerification;
import core.ShaUrl;
import core.User;
import java.util.List;

public class GitCommit extends ShaUrl {

  private static final int MAX_COMMIT_LENGHT = 8;

  public User committer;
  public List<ShaUrl> parents;
  public User author;
  public String message;
  public ShaUrl tree;
  public int comment_count;
  public GitCommitVerification verification;

  public GitCommit() {
  }

  public User getCommitter() {
    return committer;
  }

  public void setCommitter(User committer) {
    this.committer = committer;
  }

  public List<ShaUrl> getParents() {
    return parents;
  }

  public void setParents(List<ShaUrl> parents) {
    this.parents = parents;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ShaUrl getTree() {
    return tree;
  }

  public void setTree(ShaUrl tree) {
    this.tree = tree;
  }

  public int getComment_count() {
    return comment_count;
  }

  public void setComment_count(int comment_count) {
    this.comment_count = comment_count;
  }

  public GitCommitVerification getVerification() {
    return verification;
  }

  public void setVerification(GitCommitVerification verification) {
    this.verification = verification;
  }


  public String shortMessage() {
    if (message != null) {
      int start = 0;
      int end = Math.min(MAX_COMMIT_LENGHT, message.length());

      return message.substring(start, end);
    }
    return null;
  }

}
