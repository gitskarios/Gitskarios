package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 10/04/2015.
 */
public class IssueCommentView extends LinearLayout {

    private IssueComment issueComment;

    private TextView body;
    private WebView bodyHtml;
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
        bodyHtml = (WebView) findViewById(R.id.webBody);
        View authorView = findViewById(R.id.author);
        profileIcon = (ImageView) authorView.findViewById(R.id.profileIcon);
        profileName = (TextView) authorView.findViewById(R.id.name);
        profileEmail = (TextView) authorView.findViewById(R.id.email);
    }

    public void setComment(IssueStoryComment issueStoryDetail) {
        if (this.issueComment == null) {
            this.issueComment = issueStoryDetail.comment;

            if (issueComment.user != null) {
                profileName.setText(issueComment.user.login);
                profileEmail.setText(TimeUtils.getTimeString(getContext(), issueComment.created_at));
                ImageLoader instance = ImageLoader.getInstance();
                instance.displayImage(issueComment.user.avatar_url, profileIcon);
            }

            if (issueComment.body_html != null) {
                bodyHtml.loadData(issueComment.body_html, "text/html", "UTF-8");
                bodyHtml.setBackgroundColor(Color.TRANSPARENT);
                bodyHtml.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                bodyHtml.setVisibility(View.VISIBLE);
                body.setVisibility(View.GONE);
            } else if (issueComment.body != null) {
                body.setText(issueComment.body);
                body.setVisibility(View.VISIBLE);
                bodyHtml.setVisibility(View.GONE);
            } else {
                body.setVisibility(View.GONE);
                bodyHtml.setVisibility(View.GONE);
            }


        }
    }
}
