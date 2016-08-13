package com.alorma.github.ui.fragment.pullrequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.sdk.bean.dto.request.EditIssueLabelsRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.ChangeAssigneeAction;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.issue.IssueLabelsActivity;
import com.alorma.github.ui.activity.issue.RepositoryMilestonesActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.ui.view.issue.AssigneesAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nineoldandroids.animation.ValueAnimator;
import com.wefika.flowlayout.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PullRequestInfoFragment extends BaseFragment {

  private static final String EXTRA_PULL_REQUEST = "EXTRA_PULL_REQUEST";
  private static final String ISSUE_INFO = "ISSUE_INFO";

  private static final int MILESTONE_EDIT = 1234;
  private static final int LABELS_EDIT = 1235;

  @BindView(R.id.labels) ViewGroup labelsView;
  @BindView(R.id.milestone) TextView milestoneView;
  @BindView(R.id.progressMilestone) ProgressBar progressMilestone;
  @BindView(R.id.assignees) RecyclerView assigneesView;
  @BindView(R.id.branch_head) TextView branchHeadView;
  @BindView(R.id.branch_base) TextView branchBaseView;
  @BindView(R.id.toolbar_labels_title) Toolbar toolbarLabels;
  @BindView(R.id.toolbar_milestone) Toolbar toolbarMilestone;
  @BindView(R.id.toolbar_assignee) Toolbar toolbarAssignee;
  @BindView(R.id.toolbar_branches) Toolbar toolbarBranches;

  private IssueInfo issueInfo;
  private ApiComponent apiComponent;

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
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pullrequest_info_fragment, null, false);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
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
      issueInfo = getArguments().getParcelable(ISSUE_INFO);
      PullRequest pr = getArguments().getParcelable(EXTRA_PULL_REQUEST);
      showPullRequest(pr);
    }
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    apiComponent = DaggerApiComponent.builder().apiModule(new ApiModule()).build();
  }

  public void setPullRequest(PullRequest pullRequest) {
    showPullRequest(pullRequest);
  }

  private void showPullRequest(PullRequest pullRequest) {
    if (getActivity() != null && pullRequest != null) {
      showAssignees(pullRequest.assignees, pullRequest.assignee);
      showMilestone(pullRequest.milestone);
      showLabels(pullRequest.labels);
      showBranches(pullRequest);
    }
  }

  private void configToolbar(Toolbar toolbar, Toolbar.OnMenuItemClickListener itemClickListener) {
    Menu menu = toolbar.getMenu();
    menu.clear();

    toolbar.inflateMenu(R.menu.pullrequest_detail_info_toolbars);
    MenuItem item = menu.findItem(R.id.action_pull_request_edit);
    item.setIcon(new IconicsDrawable(getActivity()).icon(Octicons.Icon.oct_gear).actionBar().colorRes(R.color.md_grey_500));
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

  private void showAssignees(List<User> assignees, User assignee) {
    if (assignees == null) {
      assignees = new ArrayList<>();
    }
    if (assignees.isEmpty() && assignee != null) {
      assignees.add(assignee);
    }
    if (!assignees.isEmpty()) {
      AssigneesAdapter adapter = new AssigneesAdapter(LayoutInflater.from(getActivity()));
      adapter.addAll(assignees);
      adapter.setCallback(item -> {
        Intent launcherIntent = ProfileActivity.createLauncherIntent(getActivity(), item);
        startActivity(launcherIntent);
      });
      assigneesView.setAdapter(adapter);
      assigneesView.setLayoutManager(new LinearLayoutManager(getActivity()));
      assigneesView.setVisibility(View.VISIBLE);
    } else {
      assigneesView.setVisibility(View.GONE);
    }

    configToolbar(toolbarAssignee, item -> {
      new ChangeAssigneeAction(getActivity(), apiComponent, issueInfo).setCallback(aBoolean -> {

      }).execute();
      return true;
    });
  }

  private void showLabels(List<Label> labels) {
    labelsView.removeAllViews();
    if (labels != null) {
      labelsView.setVisibility(View.VISIBLE);

      int marginHorizontal = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
      for (Label label : labels) {
        LabelView labelView = new LabelView(getContext());
        labelView.setLabel(label);
        labelsView.addView(labelView);

        if (labelView.getLayoutParams() != null && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
          FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) labelView.getLayoutParams();
          layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.setMargins(marginHorizontal, marginHorizontal, marginHorizontal, marginHorizontal);
          labelView.setLayoutParams(layoutParams);
        }
      }
    } else {
      labelsView.setVisibility(View.GONE);
    }

    configToolbar(toolbarLabels, item -> {
      Intent intent = IssueLabelsActivity.createLauncher(getActivity(), issueInfo, labels, true);
      startActivityForResult(intent, LABELS_EDIT);
      return true;
    });
  }

  private void showMilestone(Milestone milestone) {
    if (milestone != null) {
      progressMilestone.setVisibility(View.VISIBLE);
      milestoneView.setVisibility(View.VISIBLE);
      milestoneView.setText(milestone.title);
      progressMilestone.setMax(milestone.closedIssues + milestone.openIssues);
      ValueAnimator animator = ValueAnimator.ofInt(0, milestone.closedIssues);
      animator.addUpdateListener(animation -> progressMilestone.setProgress((int) animation.getAnimatedValue()));
      animator.setDuration(300);
      animator.setInterpolator(new DecelerateInterpolator());
      animator.start();
    } else {
      progressMilestone.setVisibility(View.GONE);
      milestoneView.setVisibility(View.GONE);
    }
    configToolbar(toolbarMilestone, item -> {
      Intent intent = RepositoryMilestonesActivity.createLauncher(getActivity(), issueInfo.repoInfo, true);
      startActivityForResult(intent, MILESTONE_EDIT);
      return true;
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == MILESTONE_EDIT && data != null) {
        Milestone milestone = data.getParcelableExtra(Milestone.class.getSimpleName());
        showMilestone(milestone);

        EditIssueMilestoneRequestDTO dto = new EditIssueMilestoneRequestDTO();
        dto.milestone = milestone.number;
        executeEditIssue(dto);
      } else if (requestCode == LABELS_EDIT && data != null) {
        List<String> labels = data.getStringArrayListExtra(Label.class.getSimpleName());

        EditIssueLabelsRequestDTO dto = new EditIssueLabelsRequestDTO();
        dto.labels = labels.toArray(new String[labels.size()]);
        executeEditIssue(dto);
      }
    }
  }

  private void executeEditIssue(EditIssueRequestDTO editIssueRequestDTO) {
    EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);
    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Issue>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        ErrorHandler.onError(getActivity(), "PR detail", e);
      }

      @Override
      public void onNext(Issue issue) {
        showLabels(issue.labels);
        Toast.makeText(getActivity(), R.string.md_done_label, Toast.LENGTH_SHORT).show();
      }
    });
  }
}
