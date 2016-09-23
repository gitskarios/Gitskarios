package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.issue.IssueStoryLabelList;
import com.alorma.github.sdk.bean.issue.IssueStoryUnlabelList;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import core.User;
import core.issues.Label;
import java.util.List;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class IssueStoryLabelDetailView extends LinearLayout {

  private TextView userText;
  private UserAvatarView profileIcon;
  private IssueStoryLabelsView labelsView;
  private TextView createdAt;

  public IssueStoryLabelDetailView(Context context) {
    super(context);
    init();
  }

  public IssueStoryLabelDetailView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public IssueStoryLabelDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public IssueStoryLabelDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.issue_detail_labels, this);

    userText = (TextView) findViewById(R.id.userLogin);
    profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
    createdAt = (TextView) findViewById(R.id.createdAt);
    labelsView = (IssueStoryLabelsView) findViewById(R.id.labelsView);
  }

  public void setLabelsEvent(IssueStoryLabelList labelsEvent) {
    long milis = System.currentTimeMillis();
    printLabelsEvent(true, labelsEvent.created_at, labelsEvent.user(), labelsEvent);
    Log.i("PR_time_labels", (System.currentTimeMillis() - milis) + "ms");
  }

  public void setLabelsEvent(IssueStoryUnlabelList labelsEvent) {
    long milis = System.currentTimeMillis();
    printLabelsEvent(false, labelsEvent.created_at, labelsEvent.user(), labelsEvent);
    Log.i("PR_time_unlabels", (System.currentTimeMillis() - milis) + "ms");
  }

  private void printLabelsEvent(boolean added, long created_at, User user, List<Label> labels) {
    userText.setText(user.getLogin());

    profileIcon.setUser(user);
    labelsView.setLabels(labels);
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String date = TimeUtils.getTimeAgoString(formatter.print(created_at));
    String dateText =
        getContext().getResources().getString(added ? R.string.aissue_detail_add_labels : R.string.aissue_detail_removed_labels, date);
    createdAt.setText(dateText);
  }
}
