package com.alorma.github;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.ReleaseInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import java.util.Arrays;

public class GitskariosUriManager {

  public static final String[] RESERVED_KEYS = new String[] {
      "/notifications", "/settings", "/blog", "/explore", "/dashboard", "/repositories",
      "/site", "/security", "/contact", "/about", "/orgs", "/"
  };

  public boolean isReserved(String uri) throws GalimatiasParseException {
    URL url = URL.parse(uri);

    return Arrays.asList(RESERVED_KEYS).contains(url.path());
  }

  public RepoInfo getRepoInfo(String url) throws GalimatiasParseException {
    if (isReserved(url)) {
      return null;
    }
    RepoInfo repoInfo = new RepoInfo();

    URL parsedUrl = URL.parse(url);
    repoInfo.owner = parsedUrl.pathSegments().get(0);
    repoInfo.name = parsedUrl.pathSegments().get(1);

    return repoInfo;
  }

  public RepoInfo getRepoInfo(Uri uri) {
    try {
      return getRepoInfo(uri.toString());
    } catch (GalimatiasParseException e) {
      return null;
    }
  }

  public User getUser(String url) throws GalimatiasParseException {
    if (isReserved(url)) {
      return null;
    }
    User user = new User();
    URL parsedUrl = URL.parse(url);
    user.login = parsedUrl.pathSegments().get(0);
    return user;
  }

  public User getUser(Uri uri) {
    try {
      return getUser(uri.toString());
    } catch (GalimatiasParseException e) {
      return null;
    }
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
