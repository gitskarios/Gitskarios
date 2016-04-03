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
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import java.util.List;

public class IssueAssigneesActivity extends BackActivity
    implements Presenter.Callback<List<User>>, RecyclerArrayAdapter.ItemCallback<User> {

  private static final String ISSUE_INFO = "ISSUE_INFO";
  private static final String RETURN = "RETURN";

  @Bind(R.id.recycler) RecyclerView recyclerView;
  private UsersAdapter adapter;
  private boolean returnResult;

  public static Intent createLauncher(Context context, IssueInfo issueInfo, boolean returnResult) {
    Intent intent = new Intent(context, IssueAssigneesActivity.class);
    Bundle args = new Bundle();
    args.putParcelable(ISSUE_INFO, issueInfo);
    args.putBoolean(RETURN, returnResult);
    intent.putExtras(args);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_recyclerview);
    ButterKnife.bind(this);

    setTitle(R.string.collaborators_fragment_title);

    IssueInfo issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);
    returnResult = getIntent().getExtras().getBoolean(RETURN, false);

    adapter = new UsersAdapter(getLayoutInflater());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter.setCallback(this);
    adapter.setReturn(returnResult);
    recyclerView.setAdapter(adapter);

    IssueAssigneesPresenter presenter = new IssueAssigneesPresenter();
    presenter.load(issueInfo, this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<User> organizations) {
    adapter.addAll(organizations);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onItemSelected(User item) {
    if (returnResult) {
      Intent intent = new Intent();
      intent.putExtra(User.class.getSimpleName(), item);
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}
