package com.alorma.github.ui.fragment.pullrequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.view.LabelView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nineoldandroids.animation.ValueAnimator;
import com.wefika.flowlayout.FlowLayout;

public class PullRequestInfoFragment extends Fragment {

  private static final String EXTRA_PULL_REQUEST = "EXTRA_PULL_REQUEST";
  private static final String ISSUE_INFO = "ISSUE_INFO";

  @Bind(R.id.labels) ViewGroup labelsView;
  @Bind(R.id.milestone) TextView milestoneView;
  @Bind(R.id.progressMilestone) ProgressBar progressMilestone;
  @Bind(R.id.assignee) TextView assigneeView;
  @Bind(R.id.branch_head) TextView branchHeadView;
  @Bind(R.id.branch_base) TextView branchBaseView;
  @Bind(R.id.toolbar_labels_title) Toolbar toolbarLabels;
  @Bind(R.id.toolbar_milestone) Toolbar toolbarMilestone;
  @Bind(R.id.toolbar_assignee) Toolbar toolbarAssignee;
  @Bind(R.id.toolbar_branches) Toolbar toolbarBranches;

  public static PullRequestInfoFragment newInstance(IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);

    PullRequestInfoFragment fragment = new PullRequestInfoFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static Bundle newArguments(IssueInfo issueInfo, PullRequest pullRequest) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);
    bundle.putParcelable(EXTRA_PULL_REQUEST, pullRequest);
    return bundle;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pullrequest_info_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    toolbarLabels.setTitle(R.string.labels_title);
    toolbarMilestone.setTitle(R.string.milestone);
    toolbarAssignee.setTitle(R.string.assignee);
    toolbarBranches.setTitle(R.string.branches);

    if (getArguments() != null) {
      PullRequest pr = getArguments().getParcelable(EXTRA_PULL_REQUEST);
      showPullRequest(pr);
    }
  }

  public void setPullRequest(PullRequest pullRequest) {
    showPullRequest(pullRequest);
  }

  private void showPullRequest(PullRequest pullRequest) {
    if (getActivity() != null && pullRequest != null) {
      showAssignee(pullRequest);
      showMilestone(pullRequest);
      showLabels(pullRequest);
      showBranches(pullRequest);
    }
  }

  private void configToolbar(Toolbar toolbar, Toolbar.OnMenuItemClickListener itemClickListener) {
    toolbar.inflateMenu(R.menu.pullrequest_detail_info_toolbars);
    Menu menu = toolbar.getMenu();
    if (menu != null) {
      MenuItem item = menu.findItem(R.id.action_pull_request_edit);
      item.setIcon(new IconicsDrawable(getActivity()).icon(Octicons.Icon.oct_gear)
          .actionBar()
          .colorRes(R.color.md_grey_500));
    }
    if (itemClickListener != null) {
      toolbar.setOnMenuItemClickListener(itemClickListener);
    }
  }

  private void showBranches(PullRequest pullRequest) {
    if (pullRequest.head != null) {
      branchHeadView.setText(pullRequest.head.label);
    }
    if (pullRequest.base != null) {
      branchBaseView.setText(pullRequest.base.label);
    }
  }

  private void showAssignee(PullRequest pullRequest) {
    if (pullRequest.assignee != null) {
      assigneeView.setVisibility(View.VISIBLE);
      assigneeView.setText(pullRequest.assignee.login);
      assigneeView.setOnClickListener(v -> {
        Intent launcherIntent =
            ProfileActivity.createLauncherIntent(getActivity(), pullRequest.assignee);
        startActivity(launcherIntent);
      });
    } else {
      assigneeView.setVisibility(View.GONE);
    }

    configToolbar(toolbarAssignee, item -> {
      Toast.makeText(getActivity(), "Assignee", Toast.LENGTH_SHORT).show();
      return true;
    });
  }

  private void showLabels(PullRequest pullRequest) {
    if (pullRequest.labels != null) {
      labelsView.setVisibility(View.VISIBLE);
      int marginHorizontal = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
      for (Label label : pullRequest.labels) {
        LabelView labelView = new LabelView(getContext());
        labelView.setLabel(label);
        labelsView.addView(labelView);

        if (labelView.getLayoutParams() != null
            && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
          FlowLayout.LayoutParams layoutParams =
              (FlowLayout.LayoutParams) labelView.getLayoutParams();
          layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.setMargins(marginHorizontal, marginHorizontal, marginHorizontal,
              marginHorizontal);
          labelView.setLayoutParams(layoutParams);
        }
      }
    } else {
      labelsView.setVisibility(View.GONE);
    }

    configToolbar(toolbarLabels, item -> {
      Toast.makeText(getActivity(), "Labels", Toast.LENGTH_SHORT).show();
      return true;
    });
  }

  private void showMilestone(PullRequest pullRequest) {
    if (pullRequest.milestone != null) {
      progressMilestone.setVisibility(View.VISIBLE);
      milestoneView.setVisibility(View.VISIBLE);
      milestoneView.setText(pullRequest.milestone.title);
      progressMilestone.setMax(
          pullRequest.milestone.closedIssues + pullRequest.milestone.openIssues);
      ValueAnimator animator = ValueAnimator.ofInt(0, pullRequest.milestone.closedIssues);
      animator.addUpdateListener(
          animation -> progressMilestone.setProgress((int) animation.getAnimatedValue()));
      animator.setDuration(300);
      animator.setInterpolator(new DecelerateInterpolator());
      animator.start();
    } else {
      progressMilestone.setVisibility(View.GONE);
      milestoneView.setVisibility(View.GONE);
    }
    configToolbar(toolbarMilestone, item -> {
      Toast.makeText(getActivity(), "Milestone", Toast.LENGTH_SHORT).show();
      return true;
    });
  }
}
