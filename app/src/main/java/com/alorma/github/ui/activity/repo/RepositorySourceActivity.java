package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import core.repositories.Branch;

public class RepositorySourceActivity extends RepositoryThemeActivity {

  private static final String BRANCH = "BRANCH";

  public static Intent launcherIntent(Context context, Branch branch) {
    Intent intent = new Intent(context, RepositorySourceActivity.class);
    intent.putExtra(BRANCH, branch);
    return intent;
  }

  @BindView(R.id.text) TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.source_activity);
    ButterKnife.bind(this);

    if (getIntent() != null) {
      Branch branch = getIntent().getParcelableExtra(BRANCH);
      if (branch != null) {
        textView.setText(branch.name);
      }
    }
  }
}
