package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.Rename;
import com.alorma.github.sdk.core.ShaUtils;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class IssueTimelineView extends LinearLayout {

  private TextView textView;
  private TextView userText;
  private UserAvatarView profileIcon;
  private TextView createdAt;

  public IssueTimelineView(Context context) {
    super(context);
    init();
  }

  public IssueTimelineView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public IssueTimelineView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public IssueTimelineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.issue_detail_issue_timeline_view, this);
    textView = (TextView) findViewById(R.id.text);

    userText = (TextView) findViewById(R.id.userLogin);
    profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
    createdAt = (TextView) findViewById(R.id.createdAt);
  }

  public void setIssueEvent(IssueStoryEvent issueEvent) {
    applyGenericIssueStory(issueEvent);

    textView.setText("");

    String eventType = issueEvent.event.event;
    switch (eventType) {
      case "closed":
      case "reopened": {
        String text = issueEvent.event.actor.login + " " + eventType;
        textView.setText(text);
        break;
      }
      case "assigned":
      case "unassigned": {
        String text = null;
        String user = "<b>" + issueEvent.event.assignee.login + "</b>";
        if (eventType.equals("assigned")) {
          text = getResources().getString(R.string.issue_assigned, user);
        } else if (eventType.equals("unassigned")) {
          text = getResources().getString(R.string.issue_unassigned, user);
        }
        if (text != null) {
          textView.setText(Html.fromHtml(text));
        }
        break;
      }
      case "milestoned":
      case "demilestoned": {
        String text = null;
        String milestone = "<b>" + issueEvent.event.milestone.title + "</b>";
        if (eventType.equals("milestoned")) {
          text = getResources().getString(R.string.issue_milestoned, milestone);
        } else if (eventType.equals("demilestoned")) {
          text = getResources().getString(R.string.issue_demilestoned, milestone);
        }
        if (text != null) {
          textView.setText(Html.fromHtml(text));
        }
        break;
      }
      case "merged":
      case "referenced": {
        String text = null;
        String commitId = issueEvent.event.commit_id;

        String commitContent;
        if (!TextUtils.isEmpty(commitId)) {
          commitContent = ShaUtils.shortSha(commitId);
        } else {
          commitContent = "********";
        }

        String commitContentStrong = "<b>" + commitContent + "</b>";
        if (eventType.equals("merged")) {
          text = getResources().getString(R.string.issue_merged, commitContentStrong);
        } else if (eventType.equals("referenced")) {
          text = getResources().getString(R.string.issue_referenced, commitContentStrong);
        }
        if (text != null) {
          textView.setText(Html.fromHtml(text));
        }
        break;
      }
      case "renamed": {
        Rename rename = issueEvent.event.rename;
        String from = getResources().getString(R.string.issue_renamed_from, rename.from);
        String to = getResources().getString(R.string.issue_renamed_to, rename.to);
        textView.setText(Html.fromHtml(from + "<br/>" + to));
        break;
      }
      default: {
        String text = issueEvent.event.actor.login + " " + eventType;
        textView.setText(text);
        break;
      }
    }
    textView.setVisibility(View.VISIBLE);
  }

  private void applyGenericIssueStory(IssueStoryDetail storyEvent) {
    userText.setText(storyEvent.user().login);
    profileIcon.setUser(storyEvent.user());
    setTime(storyEvent.createdAt());
  }

  private void setTime(long time) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String date = TimeUtils.getTimeAgoString(formatter.print(time));
    createdAt.setText(date);
  }
}
