package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.gcm.GcmTopicsHelper;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.repository.RepoDetailModule;
import com.alorma.github.presenter.RepositoryPresenter;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.dto.request.WebHookConfigRequest;
import com.alorma.github.sdk.bean.dto.request.WebHookRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.EditRepoClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.webhooks.AddWebHookClient;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.actions.ViewInAction;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.activity.ManageRepositoryActivity;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.callbacks.DialogBranchesSubscriber;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.fragment.detail.repo.RepoAboutFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.issues.PullRequestsListFragment;
import com.alorma.github.ui.fragment.issues.RepositoryIssuesListFragment;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;
import com.alorma.github.ui.navigation.TabsNavigation;
import com.alorma.github.ui.navigation.UiNavigation;
import com.alorma.github.utils.GitskariosDownloadManager;
import com.alorma.github.utils.ShortcutUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import core.repositories.Branch;
import core.repositories.Permissions;
import core.repositories.Repo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepoDetailActivity extends RepositoryThemeActivity
    implements AdapterView.OnItemSelectedListener,
        com.alorma.github.presenter.View<Repo>, SourceListFragment.SourceCallback {

  @Inject RepositoryPresenter presenter;

  public static final String FROM_URL = "FROM_URL";
  public static final String REPO_INFO = "REPO_INFO";
  public static final String REPO_INFO_NAME = "REPO_INFO_NAME";
  public static final String REPO_INFO_OWNER = "REPO_INFO_OWNER";

  private static final int EDIT_REPO = 464;

  private Repo currentRepo;
  private RepoInfo requestRepoInfo;
  private ArrayList<Fragment> fragments;
  private RepoAboutFragment repoAboutFragment;
  private TabsNavigation navigation;

  public static Intent createLauncherIntent(Context context, RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    Intent intent = new Intent(context, RepoDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  public static Intent createShortcutLauncherIntent(Context context, RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putString(REPO_INFO_NAME, repoInfo.name);
    bundle.putString(REPO_INFO_OWNER, repoInfo.owner);

    Intent intent = new Intent(context, RepoDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    ApiComponent apiComponent =
            DaggerApiComponent.builder()
                    .applicationComponent(applicationComponent)
                    .apiModule(new ApiModule())
                    .build();
    apiComponent
            .plus(new RepoDetailModule())
            .inject(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter.attachView(this);
    setContentView(R.layout.activity_repo_detail);

    if (getIntent().getExtras() != null) {
      requestRepoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

      if (requestRepoInfo == null) {
        if (getIntent().getExtras().containsKey(REPO_INFO_NAME) && getIntent().getExtras().containsKey(REPO_INFO_OWNER)) {
          String name = getIntent().getExtras().getString(REPO_INFO_NAME);
          String owner = getIntent().getExtras().getString(REPO_INFO_OWNER);

          requestRepoInfo = new RepoInfo();
          requestRepoInfo.name = name;
          requestRepoInfo.owner = owner;
        }
      }

      if (requestRepoInfo != null) {
        if (TextUtils.isEmpty(requestRepoInfo.branch)) {
          requestRepoInfo.branch = "master";
        }
        setTitle(requestRepoInfo.name);

        navigation = new TabsNavigation();
        listFragments(navigation);
        navigation.apply(this);

        if (requestRepoInfo != null) {
          load();
        }
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  private void listFragments(List<UiNavigation.UiItem> navigation) {
    fragments = new ArrayList<>();

    repoAboutFragment = RepoAboutFragment.newInstance(requestRepoInfo);
    navigation.add(new UiNavigation.UiItem(R.string.overview_fragment_title, R.drawable.ic_home, repoAboutFragment));

    SourceListFragment sourceListFragment = SourceListFragment.newInstance(requestRepoInfo);
    sourceListFragment.setSourceCallback(this);
    navigation.add(new UiNavigation.UiItem(R.string.files_fragment_title, R.drawable.ic_file_directory, sourceListFragment));

    CommitsListFragment commitsListFragment = CommitsListFragment.newInstance(requestRepoInfo);
    navigation.add(new UiNavigation.UiItem(R.string.commits_fragment_title, R.drawable.ic_git_commit, commitsListFragment));

    RepositoryIssuesListFragment repositoryIssuesListFragment = RepositoryIssuesListFragment.newInstance(requestRepoInfo, false);
    navigation.add(new UiNavigation.UiItem(R.string.issues_fragment_title, R.drawable.ic_issue_opened, repositoryIssuesListFragment));

    PullRequestsListFragment pullRequestsListFragment = PullRequestsListFragment.newInstance(requestRepoInfo);
    navigation.add(new UiNavigation.UiItem(R.string.pulls_fragment_title, R.drawable.ic_git_pull_request, pullRequestsListFragment));

    RepositoryTagsFragment tagsFragment = RepositoryTagsFragment.newInstance(requestRepoInfo);
    navigation.add(new UiNavigation.UiItem(R.string.tags, R.drawable.ic_package, tagsFragment));

    RepoContributorsFragment repoContributorsFragment = RepoContributorsFragment.newInstance(requestRepoInfo);
    navigation.add(new UiNavigation.UiItem(R.string.contributors_fragment_title, R.drawable.ic_person, repoContributorsFragment));

    for (UiNavigation.UiItem uiItem : navigation) {
      fragments.add(uiItem.getFragment());
    }
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void showLoading() {

  }

  private void load() {
    presenter.execute(requestRepoInfo);
  }

  @Override
  public void onDataReceived(Repo repo, boolean isFromPaginated) {
    hideProgressDialog();
    if (repo != null) {
      this.currentRepo = repo;

      requestRepoInfo.permissions = repo.permissions;
      setTitle(currentRepo.name);

      if (requestRepoInfo.branch == null) {
        requestRepoInfo.branch = repo.getDefaultBranch();
      }

      invalidateOptionsMenu();

      if (getSupportActionBar() != null) {
        getSupportActionBar().setSubtitle(requestRepoInfo.branch);
      }

      this.invalidateOptionsMenu();

      if (fragments != null) {
        Permissions permissions = repo.permissions;

        if (repoAboutFragment != null) {
          repoAboutFragment.setRepository(repo);
        }

        for (Fragment fragment : fragments) {
          if (fragment.isAdded()) {

            if (fragment instanceof PermissionsManager) {
              ((PermissionsManager) fragment).setPermissions(permissions.admin, permissions.push, permissions.pull);
            }
            if (fragment instanceof BranchManager) {
              ((BranchManager) fragment).setCurrentBranch(currentRepo.getDefaultBranch());
            }
          }
        }
      }
    }
  }

  @Override
  public void hideLoading() {
  }

  @Override
  public void showError(Throwable throwable) {
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.repo_detail_activity, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

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

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

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

    return false;
  }

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

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

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
  }

  @Override
  protected void close(boolean navigateUp) {
    if (fragments != null) {
      boolean fromUrl = getIntent().getExtras().getBoolean(FROM_URL, false);
      Fragment currentFragment = fragments.get(navigation.getCurrentItem());
      if (navigateUp && fromUrl) {
        Intent upIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        finish();
      } else if (currentFragment != null && currentFragment instanceof BackManager) {
        if (((BackManager) currentFragment).onBackPressed()) {
          finish();
        }
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  @Override
  public void onSourceDownload() {
    String archive_url = currentRepo.getArchiveUrl();
    GitskariosSettings settings = new GitskariosSettings(this);
    String zipBall = getString(R.string.download_zip_value);
    String fileType = settings.getDownloadFileType(zipBall);

    archive_url = archive_url.replace("{archive_format}", fileType);
    archive_url = archive_url.replace("{/ref}", "/" + currentRepo.getDefaultBranch());

    GitskariosDownloadManager gitskariosDownloadManager = new GitskariosDownloadManager();
    gitskariosDownloadManager.download(this, archive_url,
        currentRepo.name + "_" + currentRepo.getDefaultBranch() + "." + getExtensionFromFileType(fileType), text -> {
          Snackbar snackbar = Snackbar.make(getToolbar(), getString(text), Snackbar.LENGTH_LONG);

          snackbar.setAction(getString(R.string.external_storage_permission_request_action),
              v -> gitskariosDownloadManager.openSettings(RepoDetailActivity.this));

          snackbar.show();
        });
  }

  private String getExtensionFromFileType(String fileType) {
    if ("zipball".equals(fileType)) {
      return "zip";
    } else if ("tarball".equals(fileType)) {
      return "tar.gz";
    }
    return null;
  }
}
