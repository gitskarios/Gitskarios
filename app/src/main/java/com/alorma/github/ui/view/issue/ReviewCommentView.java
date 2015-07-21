package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComment;
import com.alorma.github.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Bernat on 21/07/2015.
 */
public class ReviewCommentView extends LinearLayout {

    private ImageView profileIcon;
    private TextView userLogin;
    private TextView createdAt;

    public ReviewCommentView(Context context) {
        super(context);
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
        profileIcon = (ImageView) findViewById(R.id.profileIcon);
        userLogin = (TextView) findViewById(R.id.userLogin);
        createdAt = (TextView) findViewById(R.id.createdAt);
    }

    public void setReviewCommit(IssueStoryReviewComment reviewCommit) {
        applyGenericIssueStory(reviewCommit);
    }

    private void applyGenericIssueStory(IssueStoryDetail storyEvent) {
        userLogin.setText(storyEvent.user().login);
        ImageLoader.getInstance().displayImage(storyEvent.user().avatar_url, profileIcon);
        setTime(storyEvent.createdAt());
    }

    private void setTime(long time) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = TimeUtils.getTimeAgoString(getContext(), formatter.print(time));
        createdAt.setText(getContext().getResources().getString(R.string.comment_on_diff, date));
    }
}
