package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.PullRequestStoryCommit;
import com.alorma.github.sdk.bean.issue.PullRequestStoryCommitsList;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bernat on 20/07/2015.
 */
public class IssueTimelineSecondaryView extends LinearLayout {

    private TextView userText;
    private ImageView profileIcon;
    private TextView shaContent;

    public IssueTimelineSecondaryView(Context context) {
        super(context);
        init();
    }

    public IssueTimelineSecondaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IssueTimelineSecondaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IssueTimelineSecondaryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.issue_detail_issue_timeline_secondary_view, this);
        userText = (TextView) findViewById(R.id.userLogin);
        profileIcon = (ImageView) findViewById(R.id.profileIcon);
        shaContent = (TextView) findViewById(R.id.shaContent);
    }

    public void setCommit(PullRequestStoryCommit issueStoryDetail) {
        userText.setText(issueStoryDetail.commit.commit.shortMessage());
        if (issueStoryDetail.user().avatar_url != null) {
            ImageLoader.getInstance().displayImage(issueStoryDetail.user().avatar_url, profileIcon);
        } else if (issueStoryDetail.user().email != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(issueStoryDetail.user().email.getBytes());
                byte messageDigest[] = digest.digest();
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++)
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                String hash = hexString.toString();
                ImageLoader.getInstance().displayImage("http://www.gravatar.com/avatar/" + hash, profileIcon);
            } catch (NoSuchAlgorithmException e) {
                IconicsDrawable iconDrawable = new IconicsDrawable(profileIcon.getContext(), Octicons.Icon.oct_octoface);
                iconDrawable.color(AttributesUtils.getSecondaryTextColor(profileIcon.getContext()));
                iconDrawable.sizeDp(36);
                iconDrawable.setAlpha(128);
                profileIcon.setImageDrawable(iconDrawable);
            }
        } else {
            IconicsDrawable iconDrawable = new IconicsDrawable(profileIcon.getContext(), Octicons.Icon.oct_octoface);
            iconDrawable.color(AttributesUtils.getSecondaryTextColor(profileIcon.getContext()));
            iconDrawable.sizeDp(36);
            iconDrawable.setAlpha(128);
            profileIcon.setImageDrawable(iconDrawable);
        }

        shaContent.setText(issueStoryDetail.commit.shortSha());
    }
}
