package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.activity.ReposActivity;
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
public class IssueDetailView extends LinearLayout {

    private Issue issue;

    private TextView title;
    private TextView body;
    private ViewGroup labelsLayout;
    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileEmail;
    private TextView textMilestone;
    private TextView textAssignee;
    private TextView textRepository;
    private IssueDetailRequestListener issueDetailRequestListener;

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
        textRepository = (TextView) findViewById(R.id.textRepository);
    }

    public void setIssue(RepoInfo repoInfo, final Issue issue) {
        if (this.issue == null) {
            this.issue = issue;
            title.setText(issue.title);

            if (issue.user != null) {
                profileName.setText(issue.user.login);
                profileEmail.setText(TimeUtils.getTimeAgoString(getContext(), issue.created_at));
                ImageLoader instance = ImageLoader.getInstance();
                instance.displayImage(issue.user.avatar_url, profileIcon);
                OnClickListener issueUserClick = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launcherIntent = ProfileActivity.createLauncherIntent(v.getContext(), issue.user);
                        v.getContext().startActivity(launcherIntent);
                    }
                };
                profileName.setOnClickListener(issueUserClick);
                profileEmail.setOnClickListener(issueUserClick);
                profileIcon.setOnClickListener(issueUserClick);
            }

            if (!TextUtils.isEmpty(issue.body_html)) {
                String htmlCode = HtmlUtils.format(issue.body_html).toString();
                HttpImageGetter imageGetter = new HttpImageGetter(getContext());

                imageGetter.repoInfo(repoInfo);
                imageGetter.bind(body, htmlCode, issue.number);

                body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
            } else {
                body.setText(Html.fromHtml("<i>" + getResources().getString(R.string.no_description_provided) + "</i>"));
                body.setTextColor(getResources().getColor(R.color.gray_github_medium));
            }

            if (issue.labels != null && issue.labels.size() > 0) {
                labelsLayout.setVisibility(View.VISIBLE);
                for (Label label : issue.labels) {
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
                Milestone milestone = issue.milestone;
                if (milestone != null) {
                    textMilestone.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_milestone).actionBar().paddingDp(8).colorRes(getColorIcons()), null, null, null);
                    textMilestone.setText(milestone.title);
                    textMilestone.setVisibility(View.VISIBLE);
                } else {
                    textMilestone.setVisibility(View.GONE);
                }
            }

            if (textAssignee != null) {
                final User assignee = issue.assignee;
                if (assignee != null) {
                    textAssignee.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_person).actionBar().colorRes(getColorIcons()).paddingDp(8), null, null, null);
                    textAssignee.setText(assignee.login);
                    textMilestone.setVisibility(View.VISIBLE);
                    textAssignee.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent launcherIntent = ProfileActivity.createLauncherIntent(v.getContext(), assignee);
                            v.getContext().startActivity(launcherIntent);
                        }
                    });
                } else {
                    textAssignee.setVisibility(View.GONE);
                }
            }

            if (textRepository != null) {
                final Repo repo = issue.repository;
                if (repo != null) {
                    textRepository.setCompoundDrawables(new IconicsDrawable(getContext(), Octicons.Icon.oct_repo).actionBar().colorRes(getColorIcons()).paddingDp(8), null, null, null);
                    textRepository.setText(repo.full_name);
                    textRepository.setVisibility(View.VISIBLE);
                    textRepository.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RepoInfo repoInfo = new RepoInfo();
                            repoInfo.owner = repo.owner.login;
                            repoInfo.name = repo.name;
                            Intent launcherIntent = RepoDetailActivity.createLauncherIntent(v.getContext(), repoInfo);
                            v.getContext().startActivity(launcherIntent);
                        }
                    });
                } else {
                    textRepository.setVisibility(View.GONE);
                }
            }
        }

        StoreCredentials credentials = new StoreCredentials(getContext());
        if (repoInfo.permissions != null && repoInfo.permissions.push || issue.user.login.equals(credentials.getUserName())) {
            OnClickListener editClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (issueDetailRequestListener != null) {
                        if (v.getId() == R.id.textTitle) {
                            issueDetailRequestListener.onTitleEditRequest();
                        } else if (v.getId() == R.id.textBody){
                            issueDetailRequestListener.onContentEditRequest();
                        }
                    }
                }
            };

            title.setOnClickListener(editClickListener);
            body.setOnClickListener(editClickListener);
        }
    }

    public int getColorIcons() {
        if (issue.state == IssueState.open) {
            return R.color.issue_state_open;
        } else {
            return R.color.issue_state_close;
        }
    }

    public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
        this.issueDetailRequestListener = issueDetailRequestListener;
    }
}
