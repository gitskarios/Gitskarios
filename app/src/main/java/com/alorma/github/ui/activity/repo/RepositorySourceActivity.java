package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.detail.repo.RepositorySourceListFragment;
import core.repositories.Branch;
import core.repositories.Repo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositorySourceActivity extends RepositoryThemeActivity {

  private static final String REPO = "REPO";
  private static final String BRANCH = "BRANCH";

  public static Intent launcherIntent(Context context, Repo currentRepo, Branch branch) {
    Intent intent = new Intent(context, RepositorySourceActivity.class);
    intent.putExtra(REPO, currentRepo);
    intent.putExtra(BRANCH, branch);
    return intent;
  }

  @BindView(R.id.branchesTitle) Spinner branchesTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.source_activity);

    ButterKnife.bind(this);

    if (getIntent() != null) {
      Repo repo = getIntent().getParcelableExtra(REPO);
      Branch branch = getIntent().getParcelableExtra(BRANCH);
      if (repo != null & branch != null) {
        List<Branch> branches = repo.branches;

        int branchIndex = -1;
        for (int i = 0; i < branches.size(); i++) {
          if (!branch.name.equals(branches.get(0).name)) {
            branchIndex = i;
          }
        }

        if (branchIndex != -1) {
          branches.remove(branchIndex);
          branches.add(0, branch);
        }

        ArrayAdapter<Branch> adapter = new ArrayAdapter<>(this, R.layout.branch_title, android.R.id.text1, branches);
        branchesTitle.setAdapter(adapter);
        branchesTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Branch branch1 = branches.get(position);
            openSourceFragment(repo, branch1);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
        });
        openSourceFragment(repo, branch);
      }
    }
  }

  private void openSourceFragment(Repo repo, Branch branch) {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = repo.getOwner().getLogin();
    repoInfo.name = repo.name;
    repoInfo.branch = branch.getCommit().getSha();
    RepositorySourceListFragment sourceListFragment = RepositorySourceListFragment.newInstance(repoInfo);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, sourceListFragment);
    ft.commit();
  }
}
