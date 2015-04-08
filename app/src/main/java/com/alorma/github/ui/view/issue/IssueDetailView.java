package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 08/04/2015.
 */
public class IssueDetailView extends LinearLayout {

    private TextView title;
    private TextView autor;
    private ImageView avatar;
    private TextView body;

    public IssueDetailView(Context context) {
        super(context);
        init();
    }

    public IssueDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IssueDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IssueDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.issue_detail_issue_view, this);
        title = (TextView) findViewById(R.id.textTitle);
        autor = (TextView) findViewById(R.id.textAuthor);
        avatar = (ImageView) findViewById(R.id.avatarAuthor);
        body = (TextView) findViewById(R.id.textBody);
    }

    public void setIssue(Issue issue) {
        title.setText(issue.title);

        if (issue.user != null) {
            autor.setText(Html.fromHtml(getContext().getString(R.string.issue_created_by, issue.user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(issue.user.avatar_url, avatar);
        }

        body.setText(issue.body_html);
    }
}
