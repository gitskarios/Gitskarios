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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.wefika.flowlayout.FlowLayout;

public class IssueDetailView extends LinearLayout {

    private Issue issue;

    private TextView title;
    private TextView body;
    private ViewGroup labelsLayout;
    private UserAvatarView profileIcon;
    private TextView profileName;
    private TextView createdAt;
    private TextView textMilestone;
    private TextView textAssignee;
    private TextView textRepository;
    private IssueDetailRequestListener issueDetailRequestListener;
    private ReactionsView reactionsLy;

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
        profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
        profileName = (TextView) findViewById(R.id.name);
        createdAt = (TextView) findViewById(R.id.time);
        textMilestone = (TextView) findViewById(R.id.textMilestone);
        textAssignee = (TextView) findViewById(R.id.textAssignee);
        textRepository = (TextView) findViewById(R.id.textRepository);
        reactionsLy = (ReactionsView) findViewById(R.id.reactionsLy);
    }

    public void setIssue(RepoInfo repoInfo, final Issue issue) {
        if (this.issue == null) {
            this.issue = issue;
            parseTitle(issue);
            parseUser(issue);
            parseBody(repoInfo, issue);
            parseLabels(issue);
            parseReactions(issue);
            parseMilestone(issue);
            parseAssignee(issue);
            parseRepository(issue);
            checkEditable(repoInfo, issue);
        }
    }

    private void parseTitle(Issue issue) {
        title.setText(issue.title);
    }

    private void parseUser(Issue issue) {
        if (issue.user != null) {
            profileName.setText(issue.user.login);
            createdAt.setText(TimeUtils.getTimeAgoString(issue.created_at));

            profileIcon.setUser(issue.user);
            OnClickListener issueUserClick = v -> {
                Intent launcherIntent =
                        ProfileActivity.createLauncherIntent(v.getContext(), issue.user);
                v.getContext().startActivity(launcherIntent);
            };
            profileName.setOnClickListener(issueUserClick);
            createdAt.setOnClickListener(issueUserClick);
            profileIcon.setOnClickListener(issueUserClick);
        }
    }

    private void checkEditable(RepoInfo repoInfo, Issue issue) {
        StoreCredentials credentials = new StoreCredentials(getContext());
        if (repoInfo.permissions != null && repoInfo.permissions.push || issue.user.login.equals(
                credentials.getUserName())) {
            OnClickListener editClickListener = v -> {
                if (issueDetailRequestListener != null) {
                    if (v.getId() == R.id.textTitle) {
                        issueDetailRequestListener.onTitleEditRequest();
                    } else if (v.getId() == R.id.textBody) {
                        issueDetailRequestListener.onContentEditRequest();
                    }
                }
            };

            title.setOnClickListener(editClickListener);
            body.setOnClickListener(editClickListener);
        }
    }

    private void parseRepository(Issue issue) {
        if (textRepository != null) {
            final Repo repo = issue.repository;
            if (repo != null) {
                textRepository.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo), null, null, null);
                textRepository.setText(repo.full_name);
                textRepository.setVisibility(View.VISIBLE);
                textRepository.setOnClickListener(v -> {
                    RepoInfo repoInfo1 = new RepoInfo();
                    repoInfo1.owner = repo.owner.login;
                    repoInfo1.name = repo.name;
                    Intent launcherIntent =
                            RepoDetailActivity.createLauncherIntent(v.getContext(), repoInfo1);
                    v.getContext().startActivity(launcherIntent);
                });
            } else {
                textRepository.setVisibility(View.GONE);
            }
        }
    }

    private void parseAssignee(Issue issue) {
        if (textAssignee != null) {
            final User assignee = issue.assignee;
            if (assignee != null) {
                textAssignee.setCompoundDrawables(getIcon(Octicons.Icon.oct_person), null, null, null);
                textAssignee.setText(assignee.login);
                textAssignee.setVisibility(View.VISIBLE);
                textAssignee.setOnClickListener(v -> {
                    Intent launcherIntent =
                            ProfileActivity.createLauncherIntent(v.getContext(), assignee);
                    v.getContext().startActivity(launcherIntent);
                });
            } else {
                textAssignee.setVisibility(View.GONE);
            }
        }
    }

    private void parseMilestone(Issue issue) {
        if (textMilestone != null) {
            if (issue.milestone != null) {
                textMilestone.setCompoundDrawables(getIcon(Octicons.Icon.oct_milestone), null, null,
                        null);
                textMilestone.setText(issue.milestone.title);
                textMilestone.setVisibility(View.VISIBLE);
            } else {
                textMilestone.setVisibility(View.GONE);
            }
        }
    }

    private void parseReactions(Issue issue) {
        if (reactionsLy != null) {
            if (issue.reactions != null) {
                reactionsLy.setReactions(issue.reactions);
            } else {
                reactionsLy.setVisibility(View.GONE);
            }
        }
    }

    private void parseLabels(Issue issue) {
        if (issue.labels != null && issue.labels.size() > 0) {
            labelsLayout.setVisibility(View.VISIBLE);
            int margin = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
            for (Label label : issue.labels) {
                LabelView labelView = new LabelView(getContext());
                labelView.setLabel(label);
                labelsLayout.addView(labelView);

                if (labelView.getLayoutParams() != null
                        && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
                    FlowLayout.LayoutParams layoutParams =
                            (FlowLayout.LayoutParams) labelView.getLayoutParams();
                    layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.setMargins(margin, margin, margin, margin);
                    labelView.setLayoutParams(layoutParams);
                }
            }
        } else {
            labelsLayout.setVisibility(View.GONE);
        }
    }

    private void parseBody(RepoInfo repoInfo, Issue issue) {
        if (!TextUtils.isEmpty(issue.body_html)) {
            String htmlCode = HtmlUtils.format(issue.body_html).toString();
            HttpImageGetter imageGetter = new HttpImageGetter(getContext());

            imageGetter.repoInfo(repoInfo);
            imageGetter.bind(body, htmlCode, issue.number);

            body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
        } else {
            body.setText(Html.fromHtml(
                    "<i>" + getResources().getString(R.string.no_description_provided) + "</i>"));
            body.setTextColor(AttributesUtils.getIconsColor(getContext()));
        }
    }

    private IconicsDrawable getIcon(IIcon icon) {
        return new IconicsDrawable(getContext(), icon).actionBar().colorRes(getColorIcons());
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
