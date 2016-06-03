package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComment;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComments;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.view.DiffTextView;
import com.alorma.github.ui.view.UserAvatarView;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialize.color.Material;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.Arrays;
import java.util.List;
import tk.zielony.naturaldateformat.NaturalDateFormat;
import tk.zielony.naturaldateformat.RelativeDateFormat;

public class ReviewCommentView extends LinearLayout {

  private static final int ICON_ROUNDED_CORNER_DP = 16;
  private static final int ICON_SIZE = 30;
  private static final int ICON_PADDING = 6;
  private static final int MAX_LINES = 7;
  private ViewGroup parent;

  private UserAvatarView profileIcon;
  private TextView userLogin;
  private TextView createdAt;
  private TextView bodyText;
  private TextView textDiffFileTitle;
  private DiffTextView textDiff;

  public ReviewCommentView(Context context, ViewGroup parent) {
    super(context);
    this.parent = parent;
    init();
  }

  public ReviewCommentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ReviewCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ReviewCommentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.issue_detail_issue_timeline_review_secondary_view, this);
    profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
    userLogin = (TextView) findViewById(R.id.userLogin);
    createdAt = (TextView) findViewById(R.id.createdAt);
    bodyText = (TextView) findViewById(R.id.bodyText);
    textDiffFileTitle = (TextView) findViewById(R.id.textDiffFileTitle);
    textDiff = (DiffTextView) findViewById(R.id.textDiff);

    setOrientation(HORIZONTAL);
  }

  public void setReviewComment(IssueStoryReviewComment reviewComment, RepoInfo repoInfo) {
    long time = System.currentTimeMillis();

    ReviewComment event = reviewComment.event;

    String htmlCode = HtmlUtils.format(event.body).toString();
    HttpImageGetter imageGetter = new HttpImageGetter(getContext());
    imageGetter.repoInfo(repoInfo);
    imageGetter.bind(bodyText, htmlCode, reviewComment.hashCode());
    bodyText.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);

    textDiff.setText(splitDiffHunk(event.diff_hunk));
    textDiffFileTitle.setText(event.path);
    setIcon(Octicons.Icon.oct_code);
    userLogin.setText(reviewComment.user().login);
    createdAt.setText(getTime(reviewComment.created_at));

    textDiff.setOnClickListener(v -> {
      FileInfo info = new FileInfo();
      info.content = event.diff_hunk;
      info.name = event.path;

      Intent launcherIntent = FileActivity.createLauncherIntent(getContext(), info, false);
      getContext().startActivity(launcherIntent);
    });

    Log.i("PR_time_review", (System.currentTimeMillis() - time) + "ms");
  }

  public void setReviewComments(IssueStoryReviewComments reviewComments, RepoInfo repoInfo) {
    long time = System.currentTimeMillis();

    Pair<String, List<ReviewComment>> event = reviewComments.event;

    userLogin.setText(reviewComments.user().login);
    createdAt.setText(getTime(reviewComments.createdAt()));

    textDiff.setText(splitDiffHunk(event.first));
    textDiffFileTitle.setText(event.second.get(0).path);
    setIcon(Octicons.Icon.oct_code);

    textDiff.setOnClickListener(v -> {
      FileInfo info = new FileInfo();
      info.content = event.second.get(0).diff_hunk;
      info.name = event.second.get(0).path;

      Intent launcherIntent = FileActivity.createLauncherIntent(getContext(), info, false);
      getContext().startActivity(launcherIntent);
    });

    StringBuilder builder = new StringBuilder();

    for (ReviewComment reviewComment : event.second) {
      builder.append("<b>");
      builder.append(reviewComment.user.login);
      builder.append(":");
      builder.append("</b>");
      builder.append("<br />");
      builder.append(reviewComment.body);
      builder.append("<br />");
      builder.append("<br />");
    }

    String htmlCode = HtmlUtils.format(builder.toString()).toString();
    HttpImageGetter imageGetter = new HttpImageGetter(getContext());
    imageGetter.repoInfo(repoInfo);
    imageGetter.bind(bodyText, htmlCode, event.first.hashCode());
    bodyText.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);

    Log.i("PR_time_reviews", (System.currentTimeMillis() - time) + "ms");
  }

  private String splitDiffHunk(String hunk) {
    List<String> lines = Arrays.asList(hunk.split("\\r?\\n|\\r"));

    int min = Math.min(MAX_LINES, lines.size());

    min = Math.max(0, lines.size() - 1 - min);

    List<String> splitLines = lines.subList(min, lines.size());

    SpannableStringBuilder builder = new SpannableStringBuilder();
    for (String splitLine : splitLines) {
      builder.append(splitLine);
      builder.append("\n");
    }
    return builder.toString();
  }

  private void setIcon(IIcon icon) {
    IconicsDrawable drawable = new IconicsDrawable(getContext());
    drawable.backgroundColor(Material.Grey._300.getAsColor());
    drawable.color(Material.Grey._700.getAsColor());
    drawable.roundedCornersDp(ICON_ROUNDED_CORNER_DP);
    drawable.sizeDp(ICON_SIZE);
    drawable.paddingDp(ICON_PADDING);
    profileIcon.setImageDrawable(drawable.icon(icon));
  }

  private String getTime(long time) {
    RelativeDateFormat relFormat = new RelativeDateFormat(getContext(), NaturalDateFormat.DATE);
    return relFormat.format(time);
  }
}
