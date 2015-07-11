package com.alorma.github.ui.view.pullrequest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.StoreCredentials;
import com.alorma.github.sdk.Head;
import com.alorma.github.sdk.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.PullRequestCommitsActivity;
import com.alorma.github.ui.activity.PullRequestFilesActivity;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

/**
 * Created by Bernat on 08/04/2015.
 */
public class PullRequestDetailView extends LinearLayout {

    private Issue pullRequest;

    private TextView title;
    private TextView body;
    private ViewGroup labelsLayout;
    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileEmail;
    private TextView textMilestone;
    private TextView textAssignee;
    private TextView textCommits;
    private TextView textFiles;
    private TextView mergeButton;

    private IssueDetailRequestListener issueDetailRequestListener;
    private PullRequestActionsListener pullRequestActionsListener;

    public PullRequestDetailView(Context context) {
        super(context);
        init();
    }

    public PullRequestDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.pullrequest_detail_issue_view, this);
        setOrientation(VERTICAL);
        title = (TextView) findViewById(R.id.textTitle);
        body = (TextView) findViewById(R.id.textBody);
        labelsLayout = (ViewGroup) findViewById(R.id.labelsLayout);
        View authorView = findViewById(R.id.author);
        profileIcon = (ImageView) authorView.findViewById(R.id.profileIcon);
        profileName = (TextView) authorView.findViewById(R.id.name);
        profileEmail = (TextView) authorView.findViewById(R.id.email);
        textMilestone = (TextView) findViewById(R.id.textMilestone);
        textAssignee = (TextView) findViewById(R.id.textAssignee);
        textCommits = (TextView) findViewById(R.id.textCommits);
        textFiles = (TextView) findViewById(R.id.textFiles);
        mergeButton = (TextView) findViewById(R.id.mergeButton);
    }

    public void setPullRequest(final RepoInfo repoInfo, final PullRequest pullRequest, Permissions permissions) {
        if (this.pullRequest == null) {
            this.pullRequest = pullRequest;
            title.setText(pullRequest.title);

            if (pullRequest.user != null) {
                profileName.setText(pullRequest.user.login);
                profileEmail.setText(TimeUtils.getTimeAgoString(getContext(), pullRequest.created_at));
                ImageLoader instance = ImageLoader.getInstance();
                instance.displayImage(pullRequest.user.avatar_url, profileIcon);
            }

            if (pullRequest.body_html != null) {
                String htmlCode = HtmlUtils.format(pullRequest.body_html).toString();
                HttpImageGetter imageGetter = new HttpImageGetter(getContext());

                imageGetter.repoInfo(repoInfo);
                imageGetter.bind(body, htmlCode, pullRequest.number);

                body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
            }

            if (pullRequest.labels != null && pullRequest.labels.size() > 0) {
                labelsLayout.setVisibility(View.VISIBLE);
                for (Label label : pullRequest.labels) {
                    LabelView labelView = new LabelView(getContext());
                    labelView.setLabel(label);
                    labelsLayout.addView(labelView);

                    if (labelView.getLayoutParams() != null && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
                        int margin = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
                        FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) labelView.getLayoutParams();
                        layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.setMargins(margin, margin, margin, margin);
                        labelView.setLayoutParams(layoutParams);
                    }
                }
            } else {
                labelsLayout.setVisibility(View.GONE);
            }

            if (textMilestone != null) {
                Milestone milestone = pullRequest.milestone;
                if (milestone != null) {
                    textMilestone.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_milestone).actionBar().paddingDp(8).colorRes(getColorIcons()), null, null, null);
                    textMilestone.setText(milestone.title);
                    textMilestone.setVisibility(View.VISIBLE);
                } else {
                    textMilestone.setVisibility(View.GONE);
                }
            }

            if (textAssignee != null) {
                User assignee = pullRequest.assignee;
                if (assignee != null) {
                    textAssignee.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_person).actionBar().colorRes(getColorIcons()).paddingDp(8), null, null, null);
                    textAssignee.setText(assignee.login);
                    textMilestone.setVisibility(View.VISIBLE);
                } else {
                    textAssignee.setVisibility(View.GONE);
                }
            }

            if (textCommits != null) {
                if (pullRequest.commits > 0) {
                    textCommits.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_git_commit).actionBar().colorRes(getColorIcons()).paddingDp(8), null, null, null);
                    textCommits.setText(getResources().getString(R.string.num_of_commits, pullRequest.commits));
                    textCommits.setVisibility(View.VISIBLE);
                    textCommits.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IssueInfo info = new IssueInfo();
                            info.repoInfo = repoInfo;
                            info.num = pullRequest.number;
                            Intent intent = PullRequestCommitsActivity.launchIntent(getContext(), pullRequest, info);
                            getContext().startActivity(intent);
                        }
                    });
                } else {
                    textCommits.setVisibility(View.GONE);
                }
            }

            if (textFiles != null) {
                if (pullRequest.changed_files > 0) {
                    textFiles.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_file_binary).actionBar().colorRes(getColorIcons()).paddingDp(8), null, null, null);
                    textFiles.setText(getResources().getString(R.string.num_of_files, pullRequest.changed_files));
                    textFiles.setVisibility(View.VISIBLE);
                    textFiles.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IssueInfo info = new IssueInfo();
                            info.repoInfo = repoInfo;
                            info.num = pullRequest.number;
                            Intent intent = PullRequestFilesActivity.launchIntent(getContext(), pullRequest, info);
                            getContext().startActivity(intent);
                        }
                    });
                } else {
                    textFiles.setVisibility(View.GONE);
                }
            }

            if (mergeButton != null) {
                if (pullRequest.state == IssueState.closed || pullRequest.merged || permissions == null || !permissions.push || pullRequest.mergeable == null) {
                    mergeButton.setVisibility(View.GONE);
                } else if (pullRequest.mergeable) {
                    mergeButton.setVisibility(View.VISIBLE);
                    mergeButton.setText(R.string.pullrequest_merge_action_valid);
                    mergeButton.setBackgroundResource(R.drawable.pull_request_merge_valid);
                    mergeButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pullRequestActionsListener != null) {
                                pullRequestActionsListener.mergeRequest(pullRequest.head, pullRequest.base);
                            }
                        }
                    });
                } else {
                    mergeButton.setVisibility(View.VISIBLE);
                    mergeButton.setText(R.string.pullrequest_merge_action_invalid);
                    mergeButton.setBackgroundResource(R.drawable.pull_request_merge_invalid);
                }
            }

            StoreCredentials credentials = new StoreCredentials(getContext());
            if (repoInfo.permissions != null && repoInfo.permissions.push || pullRequest.user.login.equals(credentials.getUserName())) {
                OnClickListener editClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (issueDetailRequestListener != null) {
                            if (v.getId() == R.id.textTitle) {
                                issueDetailRequestListener.onTitleEditRequest();
                            } else if (v.getId() == R.id.textBody) {
                                issueDetailRequestListener.onContentEditRequest();
                            }
                        }
                    }
                };

                title.setOnClickListener(editClickListener);
                body.setOnClickListener(editClickListener);
            }
        }
    }

    public int getColorIcons() {
        if (pullRequest.state == IssueState.open) {
            return R.color.issue_state_open;
        } else {
            return R.color.issue_state_close;
        }
    }

    public void setPullRequestActionsListener(PullRequestActionsListener pullRequestActionsListener) {
        this.pullRequestActionsListener = pullRequestActionsListener;
    }

    public interface PullRequestActionsListener {
        void mergeRequest(Head head, Head base);
    }

    public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
        this.issueDetailRequestListener = issueDetailRequestListener;
    }
}
