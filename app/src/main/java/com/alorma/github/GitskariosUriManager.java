package com.alorma.github;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.ReleaseInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;

public class GitskariosUriManager {

  @NonNull
  public RepoInfo getRepoInfo(Uri uri) {
    RepoInfo repoInfo = new RepoInfo();

    repoInfo.owner = uri.getPathSegments().get(0);
    repoInfo.name = uri.getPathSegments().get(1);

    /*
    // TODO Branches
    if (uriMatcher.match(uri) == URI_REPO_BRANCH) {
      repoInfo.branch = uri.getLastPathSegment();
    } else if (uriMatcher.match(uri) == URI_REPO_BRANCH_FEATURE
        || uriMatcher.match(uri) == URI_REPO_BRANCH_RELEASE
        || uriMatcher.match(uri) == URI_REPO_BRANCH_HOTFIX) {
      repoInfo.branch = uri.getPathSegments().get(3) + "/" + uri.getPathSegments().get(4);
    }
    */

    repoInfo.permissions = null;
    return repoInfo;
  }

  @NonNull
  public User getUser(Uri uri) {
    User user = new User();
    user.login = uri.getLastPathSegment();
    return user;
  }

  @NonNull
  public CommitInfo getCommitInfo(Uri uri) {
    CommitInfo info = new CommitInfo();

    info.repoInfo = getRepoInfo(uri);

    info.sha = uri.getLastPathSegment();
    return info;
  }

  @NonNull
  public IssueInfo getIssueCommentInfo(Uri uri) {
    IssueInfo info = new IssueInfo();

    info.repoInfo = getRepoInfo(uri);

    String lastPathSegment = uri.getLastPathSegment();

    if (uri.getFragment() != null && uri.getFragment().contains("issuecomment-")) {
      String commentNum = uri.getFragment().replace("issuecomment-", "");
      info.commentNum = Integer.parseInt(commentNum);
    }
    info.num = Integer.parseInt(lastPathSegment);
    return info;
  }

  @NonNull
  public ReleaseInfo getReleaseInfo(Uri uri) {
    ReleaseInfo info = new ReleaseInfo(getRepoInfo(uri));

    info.num = Integer.valueOf(uri.getLastPathSegment());
    return info;
  }

  @NonNull
  public IssueInfo getIssueInfo(Uri uri) {
    IssueInfo info = new IssueInfo();

    info.repoInfo = getRepoInfo(uri);

    String lastPathSegment = uri.getLastPathSegment();

    info.num = Integer.parseInt(lastPathSegment);
    return info;
  }

  @NonNull
  public FileInfo getFileInfo(Uri uri) {
    FileInfo info = new FileInfo();

    info.repoInfo = getRepoInfo(uri);
    info.path = uri.getPath();
    if (info.path.contains(info.repoInfo.toString())) {
      info.path = info.path.replace("/" + info.repoInfo.toString() + "/blob/", "");
    }
    info.name = uri.getLastPathSegment();
    return info;
  }
}
