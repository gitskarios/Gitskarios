package com.alorma.github.ui.activity.issue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.NaturalTimeFormatter;
import java.util.List;

public class IssueMilestoneActivity extends BackActivity
    implements Presenter.Callback<List<Milestone>>, RecyclerArrayAdapter.ItemCallback<Milestone> {

  private static final String ISSUE_INFO = "ISSUE_INFO";
  private static final String RETURN = "RETURN";

  @Bind(R.id.recycler) RecyclerView recyclerView;
  private MilestonesAdapter adapter;
  private boolean returnResult;

  public static Intent createLauncher(Context context, IssueInfo issueInfo, boolean returnResult) {
    Intent intent = new Intent(context, IssueMilestoneActivity.class);
    intent.putExtra(ISSUE_INFO, issueInfo);
    intent.putExtra(RETURN, returnResult);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_recyclerview);
    ButterKnife.bind(this);

    setTitle(R.string.milestones);

    IssueInfo issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);

    returnResult = getIntent().getExtras().getBoolean(RETURN, false);

    adapter = new MilestonesAdapter(getLayoutInflater());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter.setTimeFormatter(new NaturalTimeFormatter(this));
    adapter.setCallback(this);
    recyclerView.setAdapter(adapter);

    IssueMilestonePresenter presenter = new IssueMilestonePresenter();
    presenter.load(issueInfo, this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<Milestone> milestones) {
    adapter.addAll(milestones);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onItemSelected(Milestone item) {
    if (returnResult) {
      Intent intent = new Intent();
      intent.putExtra(Milestone.class.getSimpleName(), item);
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}
