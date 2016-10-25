package com.alorma.github.ui.activity.repo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.gcm.GcmTopicsHelper;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.activity.ForksActivity;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.varunest.sparkbutton.SparkButton;
import core.User;
import core.repositories.Repo;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepoAboutFragment extends BaseFragment implements BranchManager, BackManager {

  private static final String REPO_INFO = "REPO_INFO";
  private Integer futureSubscribersCount;
  private Integer futureStarredCount;

  private RepoInfo repoInfo;
  private Repo currentRepo;
  private UserAvatarView profileIcon;

  private SparkButton starredPlaceholder;
  private TextView starredTextView;

  private SparkButton watchedPlaceholder;
  private TextView watchedTextView;

  private SparkButton forkedPlaceholder;
  private TextView forkedTextView;

  private TextView authorName;
  private View fork;
  private TextView forkOfTextView;
  private TextView createdAtTextView;

  private Boolean repoStarred = null;
  Observer<Boolean> startObserver = new Observer<Boolean>() {
    @Override
    public void onCompleted() {
      com.alorma.github.presenter.CacheWrapper.cache().removeAll();
    }

    @Override
    public void onError(Throwable e) {
      repoStarred = false;
      changeStarView();
    }

    @Override
    public void onNext(Boolean aBoolean) {
      repoStarred = aBoolean;
      changeStarView();
    }
  };

  private Boolean repoWatched = null;
  Observer<Boolean> watchObserver = new Observer<Boolean>() {
    @Override
    public void onCompleted() {
      com.alorma.github.presenter.CacheWrapper.cache().removeAll();
    }

    @Override
    public void onError(Throwable e) {
      repoWatched = false;
      changeWatchView();
    }

    @Override
    public void onNext(Boolean aBoolean) {
      repoWatched = aBoolean;
      if (aBoolean) {
        GcmTopicsHelper.registerInTopic(repoInfo);
      } else {
        GcmTopicsHelper.unregisterInTopic(repoInfo);
      }
      changeWatchView();
    }
  };

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

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    return inflater.inflate(R.layout.repo_overview_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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

    starredPlaceholder.setEventListener((button, buttonState) -> {
      starredPlaceholder.playAnimation();
      if (repoStarred != null) {
        changeStarStatus();
      }
    });
    starredTextView.setOnClickListener(v -> {
      if (repoStarred != null) {
        changeStarStatus();
      }
    });

    watchedTextView.setOnClickListener(view1 -> {
      watchedPlaceholder.playAnimation();
      if (repoWatched != null) {
        changeWatchedStatus();
      }
    });
    watchedPlaceholder.setEventListener((button, buttonState) -> changeWatchedStatus());

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

    getReadmeContent();
  }

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
    }
  }

  public void setRepository(Repo repository) {
    this.currentRepo = repository;
    if (isAdded()) {
      getStarWatchData();
      setData();
      getReadmeContent();
    }
  }

  private void getReadmeContent() {
    if (repoInfo == null) {
      loadArguments();
    }
  }

  private void setData() {
    if (getActivity() != null) {
      if (this.currentRepo != null) {
        User owner = this.currentRepo.owner;
        profileIcon.setUser(owner);
        authorName.setText(owner.getLogin());

        forkedPlaceholder.setChecked(this.currentRepo.parent != null);

        if (this.currentRepo.parent != null) {
          fork.setVisibility(View.VISIBLE);
          forkOfTextView.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo_forked, 24), null, null, null);
          forkOfTextView.setText(String.format("%s/%s", this.currentRepo.parent.owner.getLogin(), this.currentRepo.parent.name));
        }

        createdAtTextView.setCompoundDrawables(getIcon(Octicons.Icon.oct_clock, 24), null, null, null);
        createdAtTextView.setText(TimeUtils.getDateToText(getActivity(), this.currentRepo.getCreatedAt(), R.string.created_at));

        changeStarView();
        changeWatchView();

        setStarsCount(currentRepo.getStargazersCount());

        setWatchersCount(currentRepo.getSubscribersCount());

        forkedTextView.setText(String.valueOf(placeHolderNum(this.currentRepo.forks_count)));
      }
    }
  }

  private void setStarsCount(int stargazers_count) {
    starredTextView.setText(String.valueOf(placeHolderNum(stargazers_count)));
  }

  private void setWatchersCount(int subscribers_count) {
    watchedTextView.setText(String.valueOf(placeHolderNum(subscribers_count)));
  }

  private String placeHolderNum(int value) {
    NumberFormat decimalFormat = new DecimalFormat();
    return decimalFormat.format(value);
  }

  private IconicsDrawable getIcon(IIcon icon, int sizeDp) {
    return new IconicsDrawable(getActivity(), icon).color(AttributesUtils.getAccentColor(getActivity())).sizeDp(sizeDp);
  }

  @Override
  public void setCurrentBranch(String branch) {
    if (getActivity() != null) {
      repoInfo.branch = branch;
    }
  }

  @Override
  public boolean onBackPressed() {
    return true;
  }

  private void starAction(GithubClient<Boolean> starClient) {
    starClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(startObserver);
  }

  private void watchAction(GithubClient<Boolean> starClient) {
    starClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(watchObserver);
  }

  protected void getStarWatchData() {
    starAction(new CheckRepoStarredClient(currentRepo.owner.getLogin(), currentRepo.name));

    watchAction(new CheckRepoWatchedClient(currentRepo.owner.getLogin(), currentRepo.name));
  }

  private void changeStarStatus() {
    if (repoStarred != null && repoStarred) {
      futureStarredCount = currentRepo.getStargazersCount() - 1;
      starAction(new UnstarRepoClient(currentRepo.owner.getLogin(), currentRepo.name));
    } else {
      futureStarredCount = currentRepo.getStargazersCount() + 1;
      starAction(new StarRepoClient(currentRepo.owner.getLogin(), currentRepo.name));
    }
  }

  private void changeWatchedStatus() {
    if (repoWatched != null && repoWatched) {
      futureSubscribersCount = currentRepo.getSubscribersCount() - 1;
      watchAction(new UnwatchRepoClient(currentRepo.owner.getLogin(), currentRepo.name));
    } else {
      futureSubscribersCount = currentRepo.getSubscribersCount() + 1;
      watchAction(new WatchRepoClient(currentRepo.owner.getLogin(), currentRepo.name));
      watchAction(new WatchRepoClient(currentRepo.owner.getLogin(), currentRepo.name));
    }
  }

  private void changeStarView() {
    if (getActivity() != null) {
      starredPlaceholder.setChecked(repoStarred != null && repoStarred);

      if (futureStarredCount != null) {
        setStarsCount(futureStarredCount);
        currentRepo.setStargazersCount(futureStarredCount);
      }
    }
  }

  private void changeWatchView() {
    if (getActivity() != null) {
      watchedPlaceholder.setChecked(repoWatched != null && repoWatched);

      watchedPlaceholder.invalidate();

      if (futureSubscribersCount != null) {
        setWatchersCount(futureSubscribersCount);
        currentRepo.setSubscribersCount(futureSubscribersCount);
      }
    }
  }
}
