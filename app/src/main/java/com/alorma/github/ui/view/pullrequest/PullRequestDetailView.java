package com.alorma.github.ui.view.pullrequest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.PullRequestCommitsActivity;
import com.alorma.github.ui.activity.PullRequestFilesActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.activity.StatusActivity;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.wefika.flowlayout.FlowLayout;

/**
 * Created by Bernat on 08/04/2015.
 */
public class PullRequestDetailView extends LinearLayout {

    private PullRequest pullRequest;

    private TextView title;
    private TextView body;
    private ViewGroup labelsLayout;
    private UserAvatarView profileIcon;
    private TextView profileName;
    private TextView profileEmail;
    private TextView textMilestone;
    private TextView textAssignee;
    private TextView textCommits;
    private TextView textFiles;
    private TextView mergeButton;
    private TextView textRepository;
    private ImageView icon_status;
    private TextView name_status;
    private TextView description_status;
    private View status_ly;

    private IssueDetailRequestListener issueDetailRequestListener;
    private PullRequestActionsListener pullRequestActionsListener;
    private TextView textBranch;

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
        profileIcon = (UserAvatarView) authorView.findViewById(R.id.profileIcon);
        profileName = (TextView) authorView.findViewById(R.id.name);
        profileEmail = (TextView) authorView.findViewById(R.id.email);
        textMilestone = (TextView) findViewById(R.id.textMilestone);
        textAssignee = (TextView) findViewById(R.id.textAssignee);
        textCommits = (TextView) findViewById(R.id.textCommits);
        status_ly = findViewById(R.id.status_ly);
        icon_status = (ImageView) findViewById(R.id.icon_status);
        name_status = (TextView) findViewById(R.id.name_status);
        description_status = (TextView) findViewById(R.id.description_status);
        textFiles = (TextView) findViewById(R.id.textFiles);
        textRepository = (TextView) findViewById(R.id.textRepository);
        textBranch = (TextView) findViewById(R.id.textBranch);
        mergeButton = (TextView) findViewById(R.id.mergeButton);
    }

    public void setPullRequest(final RepoInfo repoInfo, final PullRequest pullRequest, GithubStatusResponse statusResponse,
                               Permissions permissions) {
        if (this.pullRequest == null) {
            this.pullRequest = pullRequest;
            title.setText(pullRequest.title);

            if (pullRequest.user != null) {
                profileName.setText(pullRequest.user.login);
                profileEmail.setText(TimeUtils.getTimeAgoString(pullRequest.created_at));

                profileIcon.setUser(pullRequest.user);

                OnClickListener issueUserClick = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launcherIntent = ProfileActivity.createLauncherIntent(v.getContext(), pullRequest.user);
                        v.getContext().startActivity(launcherIntent);
                    }
                };
                profileName.setOnClickListener(issueUserClick);
                profileEmail.setOnClickListener(issueUserClick);
                profileIcon.setOnClickListener(issueUserClick);
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
                    textMilestone.setCompoundDrawables(getIcon(Octicons.Icon.oct_milestone), null, null, null);
                    textMilestone.setText(milestone.title);
                    textMilestone.setVisibility(View.VISIBLE);
                } else {
                    textMilestone.setVisibility(View.GONE);
                }
            }

            if (textAssignee != null) {
                final User assignee = pullRequest.assignee;
                if (assignee != null) {
                    textAssignee.setCompoundDrawables(getIcon(Octicons.Icon.oct_person), null, null, null);
                    textAssignee.setText(assignee.login);
                    textAssignee.setVisibility(View.VISIBLE);
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
                final Repo repo = pullRequest.repository;
                if (repo != null) {
                    textRepository.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo), null, null, null);
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

            if (textCommits != null) {
                if (pullRequest.commits > 0) {
                    textCommits.setCompoundDrawables(getIcon(Octicons.Icon.oct_git_commit), null, null, null);
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

            if (status_ly != null) {
                if (statusResponse != null && statusResponse.total_count > 0) {
                    IIcon icon = Octicons.Icon.oct_check;
                    int background = R.drawable.github_status_circle_green;
                    int text = R.string.status_checks_success;
                    if (statusResponse.state.equals("pending")) {
                        icon = Octicons.Icon.oct_clock;
                        text = R.string.status_checks_pending;
                        background = R.drawable.github_status_circle_yellow;
                    } else if (statusResponse.state.equals("failure")) {
                        icon = Octicons.Icon.oct_x;
                        background = R.drawable.github_status_circle_red;
                        text = R.string.status_checks_failure;
                    }

                    IconicsDrawable drawable = new IconicsDrawable(icon_status.getContext(), icon).colorRes(R.color.white)
                            .sizeRes(R.dimen.material_drawer_item_primary)
                            .paddingRes(R.dimen.gapMedium);
                    icon_status.setImageDrawable(drawable);

                    icon_status.setBackgroundResource(background);

                    name_status.setText(text);
                    description_status.setText(getContext().getResources()
                            .getQuantityString(R.plurals.status_checks_num, statusResponse.total_count, statusResponse.total_count));

                    status_ly.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommitInfo commitInfo = new CommitInfo();
                            IssueInfo issueInfo = new IssueInfo();

                            commitInfo.repoInfo = repoInfo;
                            commitInfo.sha = pullRequest.head.ref;

                            issueInfo.repoInfo = repoInfo;
                            issueInfo.num = pullRequest.number;

                            Intent intent = StatusActivity.launchIntent(v.getContext(), issueInfo, commitInfo);
                            v.getContext().startActivity(intent);
                        }
                    });
                } else {
                    status_ly.setVisibility(View.GONE);
                }
            }

            if (textFiles != null) {
                if (pullRequest.changed_files > 0) {
                    textFiles.setCompoundDrawables(getIcon(Octicons.Icon.oct_file_binary), null, null, null);
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
                if (pullRequest.state == IssueState.closed
                        || pullRequest.merged
                        || permissions == null
                        || !permissions.push
                        || pullRequest.mergeable == null) {
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

            if (pullRequest.base != null && pullRequest.head != null) {
                textBranch.setCompoundDrawables(getIcon(Octicons.Icon.oct_git_branch), null, null, null);
                String branches = String.format("<b>[%s]</b> from <b>[%s]</b>", pullRequest.base.label,
                        pullRequest.head.label);
                textBranch.setText(Html.fromHtml(branches));
            }
        }
    }

    private IconicsDrawable getIcon(IIcon icon) {
        return new IconicsDrawable(getContext(), icon).actionBar().colorRes(getColorIcons());
    }

    public int getColorIcons() {
        if (pullRequest.merged) {
            return R.color.pullrequest_state_merged;
        } else if (pullRequest.state == IssueState.open) {
            return R.color.pullrequest_state_open;
        } else {
            return R.color.pullrequest_state_close;
        }
    }

    public void setPullRequestActionsListener(PullRequestActionsListener pullRequestActionsListener) {
        this.pullRequestActionsListener = pullRequestActionsListener;
    }

    public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
        this.issueDetailRequestListener = issueDetailRequestListener;
    }

    public interface PullRequestActionsListener {
        void mergeRequest(Head head, Head base);
    }
}
