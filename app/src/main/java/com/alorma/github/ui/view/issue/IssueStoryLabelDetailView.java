package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.issue.IssueStoryLabelList;
import com.alorma.github.sdk.bean.issue.IssueStoryUnlabelList;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by Bernat on 18/07/2015.
 */
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

        ViewCompat.setElevation(profileIcon, 2);
    }

    public void setLabelsEvent(IssueStoryLabelList labelsEvent) {
        printLabelsEvent(true, labelsEvent.created_at, labelsEvent.user(), labelsEvent);
    }

    public void setLabelsEvent(IssueStoryUnlabelList labelsEvent) {
        printLabelsEvent(false, labelsEvent.created_at, labelsEvent.user(), labelsEvent);
    }

    private void printLabelsEvent(boolean added, long created_at, User user, List<Label> labels) {
        userText.setText(user.login);

        profileIcon.setUser(user);
        labelsView.setLabels(labels);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = TimeUtils.getTimeAgoString(formatter.print(created_at));
        String dateText =
                getContext().getResources().getString(added ? R.string.aissue_detail_add_labels : R.string.aissue_detail_removed_labels, date);
        createdAt.setText(dateText);
    }
}
