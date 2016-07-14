package com.alorma.github.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.bean.sync.SyncFavorite;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.adapter.SyncFavoritesAdapter;
import com.alorma.github.ui.presenter.SyncFavoritesPresenter;
import java.util.List;

public class SyncFavoritesActivity extends BaseActivity implements SyncFavoritesPresenter.SyncCallbacks {

  private View addRepoButton;
  private View addIssueButton;
  private View addGistButton;

  private SyncFavoritesAdapter syncFavoritesAdapter;
  private SyncFavoritesPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sync_favorites_activity);

    addRepoButton = findViewById(R.id.addRepo);
    addIssueButton = findViewById(R.id.addIssue);
    addGistButton = findViewById(R.id.addGist);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
    if (recyclerView != null) {
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      syncFavoritesAdapter = new SyncFavoritesAdapter(getLayoutInflater());
      recyclerView.setAdapter(syncFavoritesAdapter);
    }

    presenter = new SyncFavoritesPresenter();
    presenter.setSyncCallbacks(this);
    presenter.load();
  }

  @Override
  public void enableUi() {
    addRepoButton.setEnabled(true);
    addRepoButton.setOnClickListener(v -> presenter.addRepo());
    addIssueButton.setEnabled(true);
    addIssueButton.setOnClickListener(v -> presenter.addIssue());
    addGistButton.setEnabled(true);
    addGistButton.setOnClickListener(v -> presenter.addGist());
  }

  @Override
  public void showItems(List<SyncFavorite> favorites) {
    syncFavoritesAdapter.clear();
    syncFavoritesAdapter.addAll(favorites);
  }
}
