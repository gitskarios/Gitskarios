package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Bernat on 10/04/2015.
 */
public class IssueCommentView extends LinearLayout {

    private TextView body;
    private UserAvatarView profileIcon;
    private TextView userText;
    private TextView createdAt;

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
        inflate(getContext(), R.layout.issue_detail_issue_comment_view, this);
        setOrientation(VERTICAL);
        body = (TextView) findViewById(R.id.textBody);

        userText = (TextView) findViewById(R.id.userLogin);
        profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
        createdAt = (TextView) findViewById(R.id.createdAt);
    }

    public void setComment(RepoInfo repoInfo, IssueStoryComment issueStoryDetail) {
        GithubComment githubComment = issueStoryDetail.comment;

        applyGenericIssueStory(issueStoryDetail);

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
