package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ListPopupWindow;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.repository.RepoDetailModule;
import com.alorma.github.presenter.repo.RepositoryPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.actions.ViewInAction;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.utils.RoundedBackgroundSpan;
import com.alorma.github.utils.ShortcutUtils;
import com.alorma.github.utils.TimeUtils;
import core.repositories.Branch;
import core.repositories.Repo;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RepoAboutFragment extends BaseFragment implements com.alorma.github.presenter.View<Repo> {

  private static final String REPO_INFO = "REPO_INFO";

  @Inject RepositoryPresenter presenter;

  private RepoInfo repoInfo;

  @BindView(R.id.repoDetailNumbersLayout) View repoDetailNumbersLayout;
  @BindView(R.id.repoDetailNumbersStar) Button repoDetailNumbersStar;
  @BindView(R.id.repoDetailNumbersWatch) Button repoDetailNumbersWatch;

  @BindView(R.id.repoDescriptionTextView) TextView repoDescriptionTextView;

  @BindView(R.id.repoDetailBranchesLayout) View repoDetailBranchesLayout;
  @BindView(R.id.repoDefaultBranchTextView) TextView repoDefaultBranchTextView;
  @BindView(R.id.repoDefaultBranchInfo) TextView repoDefaultBranchInfo;
  @BindView(R.id.repoDefaultBranchCodeButton) Button repoDefaultBranchCodeButton;
  @BindView(R.id.repoDefaultBranchExpandableIcon) ImageView repoDefaultBranchExpandableIcon;

  @BindView(R.id.repoDefaultOpenReadme) View repoDefaultOpenReadme;

  private Repo currentRepo;

  public static RepoAboutFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    RepoAboutFragment f = new RepoAboutFragment();
    f.setArguments(bundle);
    return f;
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    presenter.attachView(this);
  }

  @Override
  public void onDestroy() {
    presenter.detachView();
    super.onDestroy();
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);
    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new RepoDetailModule()).inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    return inflater.inflate(R.layout.repo_overview_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    loadArguments();
    presenter.execute(repoInfo);
  }

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = getArguments().getParcelable(REPO_INFO);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.repo_detail, menu);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    MenuItem shareItem = menu.findItem(R.id.share_repo);
    if (shareItem != null) {
      Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_share_mtrl_alpha);
      shareItem.setIcon(icon);
    }

    MenuItem openItem = menu.findItem(R.id.action_open_in_browser);

    if (openItem != null) {
      Drawable drawable = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_globe);
      drawable = DrawableCompat.wrap(drawable);
      DrawableCompat.setTint(drawable, Color.WHITE);
      openItem.setIcon(drawable);
    }

    /*
      if (currentRepo != null && currentRepo.permissions != null) {
        if (currentRepo.permissions.admin) {
          if (menu.findItem(R.id.action_manage_repo) == null) {
            getMenuInflater().inflate(R.menu.repo_detail_activity_permissions, menu);
          }
          if (menu.findItem(R.id.action_subscribe_push) == null) {
            getMenuInflater().inflate(R.menu.repo_detail_activity_push, menu);
          }
        }
      }
     */
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (currentRepo != null) {
      if (item.getItemId() == R.id.share_repo) {
        String title = currentRepo.getFullName();
        String url = currentRepo.svn_url;
        new ShareAction(getContext(), title, url).setType("Repository").execute();
      } else if (item.getItemId() == R.id.action_open_in_browser) {
        new ViewInAction(getContext(), currentRepo.getHtmlUrl()).setType("Repository").execute();
      } else if (item.getItemId() == R.id.action_add_shortcut) {
        ShortcutUtils.addShortcut(getContext(), repoInfo);
      }
    }
    /*

   if (item.getItemId() == R.id.action_manage_repo) {
      if (currentRepo != null) {
        Intent intent = ManageRepositoryActivity.createIntent(this, requestRepoInfo, createRepoRequest());
        startActivityForResult(intent, EDIT_REPO);
      }
    } else if (item.getItemId() == R.id.action_subscribe_push) {

    }
     */
    return super.onOptionsItemSelected(item);
  }

  /*
  private RepoRequestDTO createRepoRequest() {
    RepoRequestDTO dto = new RepoRequestDTO();

    dto.isPrivate = currentRepo.isPrivateRepo();
    dto.name = currentRepo.name;
    dto.description = currentRepo.description;
    dto.default_branch = currentRepo.getDefaultBranch();
    dto.has_downloads = currentRepo.hasDownloads;
    dto.has_wiki = currentRepo.hasWiki;
    dto.has_issues = currentRepo.hasIssues;
    dto.homepage = currentRepo.homepage;

    return dto;
  }
   */

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    /*

    if (requestCode == EDIT_REPO) {
      if (resultCode == RESULT_OK && data != null) {
        RepoRequestDTO repoRequestDTO = data.getParcelableExtra(ManageRepositoryActivity.CONTENT);
        showProgressDialog(R.string.edit_repo_loading);
        EditRepoClient editRepositoryClient = new EditRepoClient(requestRepoInfo, repoRequestDTO);
        editRepositoryClient.observable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Repo>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {

              }

              @Override
              public void onNext(Repo repo) {
                onDataReceived(repo, false);
              }
            });
      } else if (resultCode == RESULT_CANCELED) {
        finish();
      }
    }
     */
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onDataReceived(Repo repo, boolean isFromPaginated) {
    this.currentRepo = repo;

    populateDescription(repo.getDescription(), repo.getHomepage());
    populateBranches(repo.getDefaultBranchObject(), repo.getBranches());
    populateStar(repo.isStarred(), repo.getStargazersCount());
    populateWatch(repo.isWatched(), repo.getSubscribersCount());
    populateReadme();
  }

  private void populateDescription(String description, String homepage) {
    if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(homepage)) {
      repoDescriptionTextView.setText(description + " - " + homepage);
    } else if (!TextUtils.isEmpty(description)) {
      repoDescriptionTextView.setText(description);
    } else if (!TextUtils.isEmpty(homepage)) {
      repoDescriptionTextView.setText(homepage);
    } else {
      repoDescriptionTextView.setVisibility(View.GONE);
    }
  }

  private void populateBranches(Branch defaultBranch, List<Branch> branches) {
    if (defaultBranch != null) {
      repoDefaultBranchTextView.setText(defaultBranch.name);
      String timeAgoString = TimeUtils.getLongTimeAgoString(defaultBranch.commit.getCommit().getAuthor().getDate());
      String time = getResources().getString(R.string.commit_time_ago, defaultBranch.commit.author.getLogin(), timeAgoString);
      repoDefaultBranchInfo.setText(Html.fromHtml(time));
      repoDefaultBranchCodeButton.setOnClickListener(v -> openBranchCode(defaultBranch));

      if (branches != null) {
        List<Branch> visibleBranches = new ArrayList<>();
        for (Branch branch : branches) {
          if (!defaultBranch.name.equals(branch.name)) {
            visibleBranches.add(branch);
          }
        }
        if (visibleBranches.size() > 1) {
          repoDefaultBranchExpandableIcon.setOnClickListener(v -> showBranchSelector(visibleBranches));
        } else {
          repoDefaultBranchExpandableIcon.setVisibility(View.GONE);
        }
      } else {
        repoDefaultBranchExpandableIcon.setVisibility(View.GONE);
      }
    } else {
      repoDetailBranchesLayout.setVisibility(View.GONE);
    }
  }

  private void openBranchCode(Branch branch) {
    Intent intent = RepositorySourceActivity.launcherIntent(getContext(), currentRepo, branch);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }

  private void showBranchSelector(List<Branch> branches) {
    if (branches.size() > 1) {
      // TODO Show max to 5 branches, or "open all branches"
      ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
      BranchesAdapter branchesAdapter = new BranchesAdapter(getContext());
      branchesAdapter.addAll(branches);
      listPopupWindow.setAdapter(branchesAdapter);
      listPopupWindow.setAnchorView(repoDefaultBranchTextView);
      listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
        openBranchCode(branches.get(position));
        listPopupWindow.dismiss();
      });
      listPopupWindow.show();
    }
  }

  private void populateStar(boolean starred, int stargazersCount) {
    if (stargazersCount > 0) {
      @StringRes int text = R.string.repo_detail_no_starred;
      if (starred) {
        text = R.string.repo_detail_starred;
      }
      String format = " " + DecimalFormat.getNumberInstance().format(stargazersCount) + " ";
      String textStr = getString(text, format);

      int backgroundColor = ContextCompat.getColor(getContext(), R.color.md_grey_400);
      int textColor = ContextCompat.getColor(getContext(), R.color.md_grey_800);
      float radius = getResources().getDimension(R.dimen.materialize_baseline_grid_small);

      RoundedBackgroundSpan span = new RoundedBackgroundSpan(backgroundColor, textColor, radius);

      SpannableStringBuilder builder = new SpannableStringBuilder(textStr);
      builder.setSpan(span, textStr.indexOf(format), textStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      repoDetailNumbersStar.setText(builder);
    } else {
      @StringRes int text = R.string.repo_detail_starred_no_number;
      if (starred) {
        text = R.string.repo_detail_no_starred_no_number;
      }
      repoDetailNumbersStar.setText(text);
    }

    repoDetailNumbersStar.setOnClickListener(v -> presenter.toggleStar());
  }

  private void populateWatch(boolean watched, int watchedCount) {
    if (watchedCount > 0) {
      @StringRes int text = R.string.repo_detail_no_watched;
      if (watched) {
        text = R.string.repo_detail_watched;
      }
      String format = " " + DecimalFormat.getNumberInstance().format(watchedCount) + " ";
      String textStr = getString(text, format);

      int backgroundColor = ContextCompat.getColor(getContext(), R.color.md_grey_400);
      int textColor = ContextCompat.getColor(getContext(), R.color.md_grey_800);
      float radius = getResources().getDimension(R.dimen.materialize_baseline_grid_small);

      RoundedBackgroundSpan span = new RoundedBackgroundSpan(backgroundColor, textColor, radius);

      SpannableStringBuilder builder = new SpannableStringBuilder(textStr);
      builder.setSpan(span, textStr.indexOf(format), textStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      repoDetailNumbersWatch.setText(builder);
    } else {
      @StringRes int text = R.string.repo_detail_watched_no_number;
      if (watched) {
        text = R.string.repo_detail_no_watched_no_number;
      }
      repoDetailNumbersWatch.setText(text);
    }

    repoDetailNumbersWatch.setOnClickListener(v -> presenter.toggleWatch());
  }

  private void populateReadme() {
    RepoReadmeFragment repoReadmeFragment = RepoReadmeFragment.newInstance(repoInfo, true);
    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
    ft.replace(R.id.repoDetailReadmeContent, repoReadmeFragment);
    ft.commit();

    repoDefaultOpenReadme.setOnClickListener(v -> {
      Intent intent = RepoReadmeActivity.createIntent(getContext(), repoInfo);
      startActivity(intent);
    });
  }

  @Override
  public void showError(Throwable throwable) {

  }

  private class BranchesAdapter extends ArrayAdapter<Branch> {
    private final LayoutInflater inflater;

    public BranchesAdapter(Context context) {
      super(context, 0);
      inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
      TextView text = (TextView) view.findViewById(android.R.id.text1);
      text.setText(getItem(position).name);
      return view;
    }
  }
}
