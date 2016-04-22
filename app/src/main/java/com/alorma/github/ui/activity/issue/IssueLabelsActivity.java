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
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.utils.NaturalTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IssueLabelsActivity extends BackActivity implements Presenter.Callback<List<Label>> {

  private static final String ISSUE_INFO = "ISSUE_INFO";
  private static final String LABELS = "LABELS";
  private static final String RETURN = "RETURN";

  @Bind(R.id.recycler) RecyclerView recyclerView;
  private LabelsAdapter adapter;
  private boolean returnResult;

  public static Intent createLauncher(Context context, IssueInfo issueInfo, List<Label> labels,
      boolean returnResult) {
    Intent intent = new Intent(context, IssueLabelsActivity.class);
    Bundle args = new Bundle();
    args.putParcelable(ISSUE_INFO, issueInfo);
    args.putParcelableArrayList(LABELS, new ArrayList<>(labels));
    args.putBoolean(RETURN, returnResult);
    intent.putExtras(args);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_recyclerview);
    ButterKnife.bind(this);

    setTitle(R.string.labels_title);

    IssueInfo issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);
    List<Label> preSelectedLabels = getIntent().getParcelableArrayListExtra(LABELS);
    List<String> selectedLabels = new ArrayList<>(preSelectedLabels.size());

    for (Label preSelectedLabel : preSelectedLabels) {
      selectedLabels.add(preSelectedLabel.name);
    }

    returnResult = getIntent().getExtras().getBoolean(RETURN, false);

    adapter = new LabelsAdapter(getLayoutInflater(), selectedLabels);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter.setTimeFormatter(new NaturalTimeFormatter(this));
    recyclerView.setAdapter(adapter);

    IssueLabelsPresenter presenter = new IssueLabelsPresenter();
    presenter.load(issueInfo, this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<Label> labels) {
    List<LabelUiModel> labelUiModels = new ArrayList<>(labels.size());

    for (Label label : labels) {
      labelUiModels.add(new LabelUiModel(label));
    }

    adapter.addAll(labelUiModels);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onResponseEmpty() {

  }

  @Override
  protected void close(boolean navigateUp) {
    if (returnResult) {
      Set<String> selectedLabels = adapter.getSelectedLabels();
      Intent intent = new Intent();
      intent.putExtra(Label.class.getSimpleName(), new ArrayList<>(selectedLabels));
      setResult(RESULT_OK, intent);
      finish();
    } else {
      super.close(navigateUp);
    }
  }
}
