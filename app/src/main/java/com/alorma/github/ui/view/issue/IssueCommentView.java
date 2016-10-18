package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.ui.listeners.IssueCommentRequestListener;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import core.GithubComment;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class IssueCommentView extends LinearLayout {

  private IssueCommentRequestListener issueCommentRequestListener;

  private TextView body;
  private UserAvatarView profileIcon;
  private TextView userText;
  private TextView createdAt;
  private ReactionsView reactions;
  private View editComment;

  public IssueCommentView(Context context) {
    super(context);
    init();
  }

  public IssueCommentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public IssueCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public IssueCommentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.issue_card, this);
    setOrientation(VERTICAL);
    body = (TextView) findViewById(R.id.textBody);

    userText = (TextView) findViewById(R.id.name);
    profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
    createdAt = (TextView) findViewById(R.id.time);
    reactions = (ReactionsView) findViewById(R.id.reactionsLy);
    editComment = findViewById(R.id.editComment);
  }

  public void setComment(RepoInfo repoInfo, IssueStoryComment issueStoryDetail) {
    long milis = System.currentTimeMillis();
    GithubComment githubComment = issueStoryDetail.comment;

    applyGenericIssueStory(issueStoryDetail);

    if (reactions != null) {
      reactions.setReactions(githubComment.reactions);
    }

    if (githubComment.user != null) {
      profileIcon.setUser(githubComment.user);
    }

    if (githubComment.body_html != null) {
      String htmlCode = HtmlUtils.format(githubComment.body_html).toString();
      HttpImageGetter imageGetter = new HttpImageGetter(getContext());
      imageGetter.repoInfo(repoInfo);
      imageGetter.bind(body, htmlCode, githubComment.hashCode());
      body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
    }

    checkEditable(repoInfo, issueStoryDetail);
    Log.i("PR_time_comment", (System.currentTimeMillis() - milis) + "ms");
  }

  private void checkEditable(RepoInfo repoInfo, IssueStoryComment issueStoryComment) {
    StoreCredentials credentials = new StoreCredentials(getContext());
    if (repoInfo.permissions != null && repoInfo.permissions.push || issueStoryComment.user().getLogin().equals(credentials.getUserName())) {
      editComment.setVisibility(VISIBLE);
      editComment.setOnClickListener(view -> {
        if (issueCommentRequestListener != null) {
          issueCommentRequestListener.onContentEditRequest(issueStoryComment);
        }
      });
    } else {
      editComment.setVisibility(GONE);
      editComment.setOnClickListener(null);
    }
  }

  private void applyGenericIssueStory(IssueStoryDetail storyEvent) {
    userText.setText(storyEvent.user().getLogin());
    profileIcon.setUser(storyEvent.user());
    setTime(storyEvent.createdAt());
  }

  private void setTime(long time) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String date = TimeUtils.getTimeAgoString(formatter.print(time));
    createdAt.setText(date);
  }

  public void setIssueCommentRequestListener(IssueCommentRequestListener issueCommentRequestListener) {
    this.issueCommentRequestListener = issueCommentRequestListener;
  }
}
