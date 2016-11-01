package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.alorma.github.presenter.RepositoryPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import core.repositories.Branch;
import core.repositories.Repo;
import java.util.List;
import javax.inject.Inject;

public class RepoAboutFragment extends BaseFragment implements com.alorma.github.presenter.View<Repo> {

  private static final int EDIT_REPO = 464;
  private static final String REPO_INFO = "REPO_INFO";

  @Inject RepositoryPresenter presenter;

  private RepoInfo repoInfo;
  private Repo currentRepo;

  @BindView(R.id.repoDescriptionTextView) TextView repoDescriptionTextView;

  @BindView(R.id.repoDetailBranchesLayout) View repoDetailBranchesLayout;
  @BindView(R.id.repoDefaultBranchTextView) TextView repoDefaultBranchTextView;
  @BindView(R.id.repoDefaultBranchExpandableIcon) ImageView repoDefaultBranchExpandableIcon;

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

    /*

    View author = view.findViewById(R.id.author);
    profileIcon = (UserAvatarView) author.findViewById(R.id.profileIcon);
    authorName = (TextView) author.findViewById(R.id.authorName);

    fork = view.findViewById(R.id.fork);
    forkOfTextView = (TextView) fork.findViewById(R.id.forkOf);

    createdAtTextView = (TextView) view.findViewById(R.id.createdAt);

    starredPlaceholder = (SparkButton) view.findViewById(R.id.starredPlaceholder);
    starredTextView = (TextView) view.findViewById(R.id.starredTextView);

    watchedPlaceholder = (SparkButton) view.findViewById(R.id.watchedPlaceHolder);
    watchedTextView = (TextView) view.findViewById(R.id.watchedTextView);

    forkedPlaceholder = (SparkButton) view.findViewById(R.id.forkedPlaceholder);
    forkedTextView = (TextView) view.findViewById(R.id.forkedTextView);

    starredPlaceholder.setEventListener((button, buttonState) -> changeStarStatus());
    watchedPlaceholder.setEventListener((button, buttonState) -> changeWatchedStatus());
    starredTextView.setOnClickListener(v -> changeStarStatus());
    watchedTextView.setOnClickListener(v -> changeWatchedStatus());

    forkedTextView.setOnClickListener(v -> {
      if (repoInfo != null) {
        Intent intent = ForksActivity.launchIntent(v.getContext(), repoInfo);
        startActivity(intent);
      }
    });

    fork.setOnClickListener(v -> {
      if (currentRepo != null && currentRepo.parent != null) {
        RepoInfo repoInfo1 = new RepoInfo();
        repoInfo1.owner = currentRepo.parent.owner.getLogin();
        repoInfo1.name = currentRepo.parent.name;
        if (!TextUtils.isEmpty(currentRepo.getDefaultBranch())) {
          repoInfo1.branch = currentRepo.getDefaultBranch();
        }

        Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), repoInfo1);
        startActivity(intent);
      }
    });

    author.setOnClickListener(v -> {
      if (currentRepo != null && currentRepo.owner != null) {
        if (currentRepo.owner.getType().equals(UserType.User.name())) {
          Intent intent = ProfileActivity.createLauncherIntent(getActivity(), currentRepo.owner);
          startActivity(intent);
        } else if (currentRepo.owner.getType().equals(UserType.Organization.name())) {
          Intent intent = OrganizationActivity.launchIntent(getActivity(), currentRepo.owner.getLogin());
          startActivity(intent);
        }
      }
    });
    */

    loadArguments();
    presenter.execute(repoInfo);
  }

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    /*
    getMenuInflater().inflate(R.menu.repo_detail_activity, menu);
     */
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    /*
    if (menu != null) {
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

      MenuItem item = menu.findItem(R.id.share_repo);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        item.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha, getTheme()));
      } else {
        item.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha));
      }

      MenuItem menuChangeBranch = menu.findItem(R.id.action_repo_change_branch);

      if (menuChangeBranch != null) {
        if (currentRepo != null && currentRepo.branches != null && currentRepo.branches.size() > 1) {
          Drawable changeBranch = new IconicsDrawable(this, Octicons.Icon.oct_git_branch).actionBar().colorRes(R.color.white);

          menuChangeBranch.setIcon(changeBranch);
        } else {
          menu.removeItem(R.id.action_repo_change_branch);
        }
      }
    }
     */
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    /*

    if (item.getItemId() == android.R.id.home) {
      finish();
    } else if (item.getItemId() == R.id.share_repo) {
      if (currentRepo != null) {
        String title = currentRepo.getFullName();
        String url = currentRepo.svn_url;

        new ShareAction(this, title, url).setType("Repository").execute();
      }
    } else if (item.getItemId() == R.id.action_open_in_browser) {
      if (currentRepo != null) {
        new ViewInAction(this, currentRepo.getHtmlUrl()).setType("Repository").execute();
      }
    } else if (item.getItemId() == R.id.action_repo_change_branch) {
      changeBranch();
    } else if (item.getItemId() == R.id.action_manage_repo) {
      if (currentRepo != null) {
        Intent intent = ManageRepositoryActivity.createIntent(this, requestRepoInfo, createRepoRequest());
        startActivityForResult(intent, EDIT_REPO);
      }
    } else if (item.getItemId() == R.id.action_add_shortcut) {
      ShortcutUtils.addShortcut(this, requestRepoInfo);
    } else if (item.getItemId() == R.id.action_subscribe_push) {
      WebHookRequest webhook = new WebHookRequest();
      webhook.name = "web";
      webhook.active = true;
      webhook.events = new String[] {
          "issues"
      };
      webhook.config = new WebHookConfigRequest();
      webhook.config.content_type = "json";
      webhook.config.url = "https://cryptic-ravine-97684.herokuapp.com/message";

      new AddWebHookClient(requestRepoInfo.owner, requestRepoInfo.name, webhook).observable()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(webHookResponse -> {
            GcmTopicsHelper.registerInTopic(requestRepoInfo);
          }, throwable -> {

          });
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

  private void changeBranch() {
    GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(requestRepoInfo);
    Observable<List<Branch>> apiObservable = repoBranchesClient.observable()
        .subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(branches -> {
          if (currentRepo != null) {
            if (currentRepo.branches != null) {
              currentRepo.branches.addAll(branches);
            } else {
              currentRepo.branches = branches;
            }
            CacheWrapper.setRepository(currentRepo);
          }
        });

    Observable<List<Branch>> memCacheObservable = Observable.create(new Observable.OnSubscribe<List<Branch>>() {
      @Override
      public void call(Subscriber<? super List<Branch>> subscriber) {
        try {
          if (!subscriber.isUnsubscribed()) {
            if (currentRepo != null && currentRepo.branches != null) {
              subscriber.onNext(currentRepo.branches);
            }
          }
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    });

    Observable.concat(memCacheObservable, apiObservable).first().subscribe(new DialogBranchesSubscriber(this, requestRepoInfo) {
      @Override
      protected void onNoBranches() {

      }

      @Override
      protected void onBranchSelected(String branch) {
        requestRepoInfo.branch = branch;
        if (currentRepo != null) {
          currentRepo.setDefaultBranch(branch);
        }
        if (getSupportActionBar() != null) {
          getSupportActionBar().setSubtitle(branch);
        }
        for (Fragment fragment : fragments) {
          if (fragment instanceof BranchManager) {
            ((BranchManager) fragment).setCurrentBranch(branch);
          }
        }
      }
    });
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
    populateBranches(repo.getDefaultBranch(), repo.getBranches());
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

  private void populateBranches(String defaultBranch, List<Branch> branches) {
    if (!TextUtils.isEmpty(defaultBranch)) {
      repoDetailBranchesLayout.setVisibility(View.VISIBLE);
      repoDefaultBranchTextView.setText(defaultBranch);

      if (branches != null && branches.size() > 1) {
        repoDefaultBranchExpandableIcon.setVisibility(View.VISIBLE);
        repoDefaultBranchExpandableIcon.setOnClickListener(v -> showBranchSelector(branches));
      } else {
        repoDefaultBranchExpandableIcon.setVisibility(View.GONE);
      }
    } else {
      repoDetailBranchesLayout.setVisibility(View.GONE);
    }
  }

  private void showBranchSelector(List<Branch> branches) {
    if (branches.size() > 1) {
      // TODO Show max to 5 branches, or "open all branches"
      ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
      BranchesAdpter branchesAdpter = new BranchesAdpter(getContext());
      branchesAdpter.addAll(branches);
      listPopupWindow.setAdapter(branchesAdpter);
      listPopupWindow.setAnchorView(repoDefaultBranchTextView);
      listPopupWindow.show();
    }
  }

  @Override
  public void showError(Throwable throwable) {

  }

  private class BranchesAdpter extends ArrayAdapter<Branch> {
    private final LayoutInflater inflater;

    public BranchesAdpter(Context context) {
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
