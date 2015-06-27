package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 10/04/2015.
 */
public class IssueCommentView extends LinearLayout {

    private TextView body;
    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileEmail;

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
        View authorView = findViewById(R.id.author);
        profileIcon = (ImageView) authorView.findViewById(R.id.profileIcon);
        profileName = (TextView) authorView.findViewById(R.id.name);
        profileEmail = (TextView) authorView.findViewById(R.id.email);
    }

    public void setComment(RepoInfo repoInfo, IssueStoryComment issueStoryDetail) {
        GithubComment githubComment = issueStoryDetail.comment;

        if (githubComment.user != null) {
            profileName.setText(githubComment.user.login);
            profileEmail.setText(TimeUtils.getTimeAgoString(getContext(), githubComment.created_at));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(githubComment.user.avatar_url, profileIcon);
        }

        if (githubComment.body_html != null) {
            String htmlCode = HtmlUtils.format(githubComment.body_html).toString();
            HttpImageGetter imageGetter = new HttpImageGetter(getContext());
            imageGetter.repoInfo(repoInfo);
            imageGetter.bind(body, htmlCode, githubComment.hashCode());
            body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
        }
    }
}
