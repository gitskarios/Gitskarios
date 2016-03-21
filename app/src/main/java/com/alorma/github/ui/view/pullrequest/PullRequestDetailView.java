package com.alorma.github.ui.view.pullrequest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

public class PullRequestDetailView extends LinearLayout {

  private PullRequest pullRequest;

  private TextView title;
  private TextView body;
  private UserAvatarView profileIcon;
  private TextView profileName;
  private TextView profileEmail;
  private TextView mergeButton;

  private IssueDetailRequestListener issueDetailRequestListener;
  private PullRequestActionsListener pullRequestActionsListener;

  public PullRequestDetailView(Context context) {
    super(context);
    init();
  }

  public PullRequestDetailView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.pullrequest_detail_issue_view, this);
    setOrientation(VERTICAL);
    title = (TextView) findViewById(R.id.textTitle);
    body = (TextView) findViewById(R.id.textBody);
    View authorView = findViewById(R.id.author);
    profileIcon = (UserAvatarView) authorView.findViewById(R.id.profileIcon);
    profileName = (TextView) authorView.findViewById(R.id.name);
    profileEmail = (TextView) authorView.findViewById(R.id.email);
    mergeButton = (TextView) findViewById(R.id.mergeButton);
  }

  public void setPullRequest(final RepoInfo repoInfo, final PullRequest pullRequest, Permissions permissions) {
    if (this.pullRequest == null) {
      this.pullRequest = pullRequest;
      title.setText(pullRequest.title);

      if (pullRequest.user != null) {
        profileName.setText(pullRequest.user.login);
        profileEmail.setText(TimeUtils.getTimeAgoString(pullRequest.created_at));

        profileIcon.setUser(pullRequest.user);

        OnClickListener issueUserClick = new OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent launcherIntent =
                ProfileActivity.createLauncherIntent(v.getContext(), pullRequest.user);
            v.getContext().startActivity(launcherIntent);
          }
        };
        profileName.setOnClickListener(issueUserClick);
        profileEmail.setOnClickListener(issueUserClick);
        profileIcon.setOnClickListener(issueUserClick);
      }

      if (pullRequest.body_html != null) {
        String htmlCode = HtmlUtils.format(pullRequest.body_html).toString();
        HttpImageGetter imageGetter = new HttpImageGetter(getContext());

        imageGetter.repoInfo(repoInfo);
        imageGetter.bind(body, htmlCode, pullRequest.number);

        body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
      }

      if (mergeButton != null) {
        if (pullRequest.state == IssueState.closed
            || pullRequest.merged
            || permissions == null
            || !permissions.push
            || pullRequest.mergeable == null) {
          mergeButton.setVisibility(View.GONE);
        } else if (pullRequest.mergeable) {
          mergeButton.setVisibility(View.VISIBLE);
          mergeButton.setText(R.string.pullrequest_merge_action_valid);
          mergeButton.setBackgroundResource(R.drawable.pull_request_merge_valid);
          mergeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              if (pullRequestActionsListener != null) {
                pullRequestActionsListener.mergeRequest(pullRequest.head, pullRequest.base);
              }
            }
          });
        } else {
          mergeButton.setVisibility(View.VISIBLE);
          mergeButton.setText(R.string.pullrequest_merge_action_invalid);
          mergeButton.setBackgroundResource(R.drawable.pull_request_merge_invalid);
        }
      }

      StoreCredentials credentials = new StoreCredentials(getContext());
      if (repoInfo.permissions != null && repoInfo.permissions.push
          || pullRequest.user.login.equals(credentials.getUserName())) {
        OnClickListener editClickListener = new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (issueDetailRequestListener != null) {
              if (v.getId() == R.id.textTitle) {
                issueDetailRequestListener.onTitleEditRequest();
              } else if (v.getId() == R.id.textBody) {
                issueDetailRequestListener.onContentEditRequest();
              }
            }
          }
        };

        title.setOnClickListener(editClickListener);
        body.setOnClickListener(editClickListener);
      }
    }
  }

  private IconicsDrawable getIcon(IIcon icon) {
    return new IconicsDrawable(getContext(), icon).actionBar().colorRes(getColorIcons());
  }

  public int getColorIcons() {
    if (pullRequest.merged) {
      return R.color.pullrequest_state_merged;
    } else if (pullRequest.state == IssueState.open) {
      return R.color.pullrequest_state_open;
    } else {
      return R.color.pullrequest_state_close;
    }
  }

  public void setPullRequestActionsListener(PullRequestActionsListener pullRequestActionsListener) {
    this.pullRequestActionsListener = pullRequestActionsListener;
  }

  public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
    this.issueDetailRequestListener = issueDetailRequestListener;
  }

  public interface PullRequestActionsListener {
    void mergeRequest(Head head, Head base);
  }
}
