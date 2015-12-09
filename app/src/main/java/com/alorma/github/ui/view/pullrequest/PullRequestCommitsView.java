package com.alorma.github.ui.view.pullrequest;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.issue.PullRequestStoryCommitsList;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Bernat on 20/07/2015.
 */
public class PullRequestCommitsView extends LinearLayout {

  private TextView userText;
  private ImageView profileIcon;
  private TextView createdAt;
  private ViewGroup commitsView;

  public PullRequestCommitsView(Context context) {
    super(context);
    init();
  }

  public PullRequestCommitsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PullRequestCommitsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PullRequestCommitsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.pull_request_commits, this);

    userText = (TextView) findViewById(R.id.userLogin);
    profileIcon = (ImageView) findViewById(R.id.profileIcon);
    createdAt = (TextView) findViewById(R.id.createdAt);
    commitsView = (ViewGroup) findViewById(R.id.commitsView);

    ViewCompat.setElevation(profileIcon, 2);
  }

  public void setPullRequestStoryCommitsList(PullRequestStoryCommitsList pullRequestStoryCommitsList) {
    userText.setText(pullRequestStoryCommitsList.user().login);

    UniversalImageLoaderUtils.loadUserAvatar(profileIcon, pullRequestStoryCommitsList.user());

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String date = TimeUtils.getTimeAgoString(formatter.print(pullRequestStoryCommitsList.createdAt()));
    String pushedDate = getContext().getResources().getString(R.string.pushed) + " " + date;

    createdAt.setText(pushedDate);

    commitsView.removeAllViews();

    for (Commit commit : pullRequestStoryCommitsList) {
      TextView textView = new TextView(getContext());

      textView.setText(Html.fromHtml("* " + "<b>" + commit.shortSha() + "</b> " + commit.commit.message));

      commitsView.addView(textView);
    }
  }
}
